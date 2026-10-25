import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.Film;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.Order;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.UnfrozenApply;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.message.Message;
import org.zlf.cinema.platfrom.util.SocketUtil;
import org.zlf.cinema.platfrom.vo.LoginResult;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 接口联调用例集：直连服务端把 4.3 的用例逐条发出。
 * 每条用例打印「发送 / 期望 / 实测 / 判定」，关键变更后另读服务端 data 目录的
 * .obj 文件做落盘旁证。只依赖 common 模块，不掺业务逻辑。
 */
public class CinemaTestRunner {

    static final String DATA_DIR = "E:/Java-demo/Cinema/cinema-platform/server-run/data/";
    static int pass = 0, fail = 0, caseNo = 0;
    static String lastFailInfo = null;

    public static void main(String[] args) throws Exception {
        System.out.println("========== 影院平台接口联调 开始 ==========");

        //---------- 注册 / 入驻 ----------
        check("注册用户", send(Command.REGISTER, new User("alice", "123456", "SA")), 1);
        check("重复注册同一账号", send(Command.REGISTER, new User("alice", "123456", "SA")), -1);
        check("入驻申请", send(Command.ENTRY_APPLY,
                new Seller("seller1", "123456", "SB", "BeijingRoad", "Wanda")), 1);
        check("影院名称重复的入驻申请", send(Command.ENTRY_APPLY,
                new Seller("seller2", "123456", "SC", "OtherRoad", "Wanda")), -1);

        //---------- 登录状态机 ----------
        LoginResult r = login("seller1", "123456");
        check("未审核商家登录", r == null ? null : r.getCode(), LoginResult.AUDITING);

        r = login("admin", "123456");
        check("管理员登录-标识", r == null ? null : r.getCode(), LoginResult.SUCCESS);
        check("管理员登录-角色", r == null || r.getUser() == null ? null : r.getUser().getRole(), User.Role.MANAGER);
        check("管理员登录-密码不回传", r == null || r.getUser() == null ? "有值" : r.getUser().getPassword(), null);

        //---------- 商家审核 ----------
        List<Seller> applies = (List<Seller>) send(Command.VIEW_ENTRY_APPLY, "y");
        check("查看待审核入驻申请(条数)", applies == null ? -99 : applies.size(), 1);
        evidence("data/user.obj", userSummary());

        check("审核商家通过", auditSeller("seller1", 1), 1);
        check("重复审核同一商家", auditSeller("seller1", 1), -2);
        evidence("data/user.obj", userSummary());

        r = login("seller1", "123456");
        check("审核后商家登录", r == null ? null : r.getCode(), LoginResult.SUCCESS);
        check("审核后商家登录-影院名", r == null || r.getUser() == null ? null
                : ((Seller) r.getUser()).getCinemaName(), "Wanda");
        Seller seller1 = (Seller) r.getUser();

        //---------- 影厅 / 影片 ----------
        check("增加影厅", send(Command.ADD_FILM_HALL, new FilmHall("HallA", 5, 5, "seller1")), 1);
        check("同名影厅", send(Command.ADD_FILM_HALL, new FilmHall("HallA", 5, 5, "seller1")), -1);
        List<FilmHall> halls = (List<FilmHall>) send(Command.VIEW_FILM_HALLS, "seller1");
        FilmHall hallA = findById(halls, "HallA");
        check("影厅初始余票", hallA == null ? -99 : hallA.getRestTickets(), 25);

        check("增加影片", send(Command.ADD_FILM, new Film("Movie1", "Actor1", "Desc", "seller1")), 1);
        check("重复影片", send(Command.ADD_FILM, new Film("Movie1", "Actor1", "Desc", "seller1")), -1);
        Map<String, Object> filmQuery = new HashMap<>();
        filmQuery.put("owner", "seller1");
        filmQuery.put("name", "Mov");
        List<Film> films = (List<Film>) send(Command.VIEW_FILMS, filmQuery);
        check("按'Mov'模糊查影片(条数)", films == null ? -99 : films.size(), 1);
        Film movie1 = films.get(0);

        //---------- 影片计划与时间冲突 ----------
        check("增加影片计划 10:00~12:00",
                send(Command.ADD_FILM_PLAN, new FilmPlan(movie1, hallA, "2026-05-01", "10:00:00", "12:00:00", seller1)), 1);
        check("11:00~13:00 与 10:00~12:00 冲突",
                send(Command.ADD_FILM_PLAN, new FilmPlan(movie1, hallA, "2026-05-01", "11:00:00", "13:00:00", seller1)), -1);
        check("12:00~14:00 首尾相接不算冲突",
                send(Command.ADD_FILM_PLAN, new FilmPlan(movie1, hallA, "2026-05-01", "12:00:00", "14:00:00", seller1)), 1);
        check("增加第二个影厅 HallB", send(Command.ADD_FILM_HALL, new FilmHall("HallB", 5, 5, "seller1")), 1);
        FilmHall hallB = findById((List<FilmHall>) send(Command.VIEW_FILM_HALLS, "seller1"), "HallB");
        check("不同影厅同天同时间不冲突",
                send(Command.ADD_FILM_PLAN, new FilmPlan(movie1, hallB, "2026-05-01", "10:00:00", "12:00:00", seller1)), 1);

        //---------- 订座 / 订单 ----------
        r = login("alice", "123456");
        check("用户登录", r == null ? null : r.getCode(), LoginResult.SUCCESS);
        User alice = r.getUser();

        Map<String, Object> planQuery = new HashMap<>();
        planQuery.put("filmName", "y");
        List<FilmPlan> plans = (List<FilmPlan>) send(Command.VIEW_FILM_PLANS, planQuery);
        String planId = null;
        for (FilmPlan p : plans) {
            //同一天有两个影厅都有 10:00 的场次，按影厅名精确定位到 HallA 的
            if ("10:00:00".equals(p.getBeginTime()) && "HallA".equals(p.getHall().getName())) planId = p.getId();
        }
        FilmPlan plan = (FilmPlan) send(Command.GET_FILM_PLAN_BY_ID, planId);
        check("按编号查影片计划", plan == null ? null : plan.getId(), planId);

        check("订座(0,0)", orderSeat(planId, 0, 0, "alice"), 1);
        evidence("data/filmPlan.obj", planSeatSummary(planId));
        check("越界订座(row=99)", orderSeat(planId, 99, 0, "alice"), 0);
        check("重复订同一座位", orderSeat(planId, 0, 0, "alice"), -2);
        plan = (FilmPlan) send(Command.GET_FILM_PLAN_BY_ID, planId);
        check("订座后余票", plan == null ? -99 : plan.getHall().getRestTickets(), 24);

        List<Order> aliceOrders = (List<Order>) send(Command.VIEW_ORDERS, alice);
        check("用户查看订单(条数)", aliceOrders == null ? -99 : aliceOrders.size(), 1);
        List<Order> sellerOrders = (List<Order>) send(Command.VIEW_ORDERS, seller1);
        check("商家查看订单(条数)", sellerOrders == null ? -99 : sellerOrders.size(), 1);
        String orderId = aliceOrders.get(0).getId();

        check("修改订单换到(1,1)", updateOrder(orderId, 1, 1, "alice"), 1);
        Order after = (Order) send(Command.GET_ORDER_BY_ID, orderId);
        check("订单座位已变为1-1", after == null ? null : after.getRow() + "-" + after.getCol(), "1-1");

        check("取消订单", cancelOrder(orderId, "alice"), 1);
        evidence("data/order.obj", orderSummary(orderId));
        check("重复取消", cancelOrder(orderId, "alice"), -2);
        plan = (FilmPlan) send(Command.GET_FILM_PLAN_BY_ID, planId);
        check("退订后余票(座位已释放)", plan == null ? -99 : plan.getHall().getRestTickets(), 25);

        //---------- 冻结 / 解冻 ----------
        check("冻结用户", send(Command.FROZEN_USER, "alice"), 1);
        evidence("data/user.obj", userSummary());
        r = login("alice", "123456");
        check("冻结后登录", r == null ? null : r.getCode(), LoginResult.FROZEN);

        check("提交解冻申请", send(Command.UNFROZEN_APPLY, new UnfrozenApply("alice", "再给我一次机会")), 1);
        check("重复提交解冻申请", send(Command.UNFROZEN_APPLY, new UnfrozenApply("alice", "再给我一次机会")), -3);

        List<UnfrozenApply> applyList = (List<UnfrozenApply>) send(Command.VIEW_UNFROZEN_APPLIES, null);
        String applyId = applyList.get(0).getId();
        Map<String, Object> audit = new HashMap<>();
        audit.put("id", applyId);
        audit.put("state", 1);
        check("审批解冻通过", send(Command.AUDIT_UNFROZEN_APPLY, audit), 1);
        r = login("alice", "123456");
        check("解冻后登录", r == null ? null : r.getCode(), LoginResult.SUCCESS);
        evidence("data/user.obj", userSummary());

        //---------- 找回密码（已知缺陷：== 比较，第一条预期失败） ----------
        System.out.println("---- 以下第一条约定为已知缺陷场景 ----");
        check("找回密码-安全码正确", send(Command.GET_PASSWORD_BACK, new User("alice", null, "SA")), "123456");
        check("找回密码-安全码错误", send(Command.GET_PASSWORD_BACK, new User("alice", null, "XX")), null);

        List<User> users = (List<User>) send(Command.VIEW_USERS, null);
        check("查看用户列表(不含商家)", users == null ? -99 : users.size(), 2);

        check("冻结商家", send(Command.FROZEN_SELLER, "seller1"), 1);
        r = login("seller1", "123456");
        check("冻结商家后登录", r == null ? null : r.getCode(), LoginResult.FROZEN);

        //---------- 汇总 ----------
        System.out.println("========== 影院平台接口联调 结束 ==========");
        System.out.println("PASS=" + pass + "  FAIL=" + fail);
        if (fail > 0) {
            System.out.println("失败用例：");
            for (String f : failures) System.out.println("  " + f);
        }
    }

    //================= 发送与断言 =================

    static Object send(int command, Object data) throws Exception {
        Socket socket = new Socket("localhost", 8888);
        SocketUtil.sendMsg(socket, new Message(command, data));
        //服务端回发的是裸结果对象，不包 Message
        Object resp = SocketUtil.receiveMsg(socket);
        socket.close();
        return resp;
    }

    static LoginResult login(String username, String password) throws Exception {
        return (LoginResult) send(Command.LOGIN, new User(username, password, null));
    }

    static Integer auditSeller(String username, int state) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("state", state);
        return (Integer) send(Command.AUDIT_SELLER, data);
    }

    static Integer orderSeat(String id, int row, int col, String owner) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("row", row);
        data.put("col", col);
        data.put("owner", owner);
        return (Integer) send(Command.ORDER_SEAT_ONLINE, data);
    }

    static Integer updateOrder(String id, int row, int col, String owner) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("row", row);
        data.put("col", col);
        data.put("owner", owner);
        return (Integer) send(Command.UPDATE_ORDER, data);
    }

    static Integer cancelOrder(String id, String owner) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("owner", owner);
        return (Integer) send(Command.CANCEL_ORDER, data);
    }

    @SuppressWarnings("unchecked")
    static <T> T findById(List<T> list, String name) {
        for (T t : list) {
            if (t instanceof FilmHall && ((FilmHall) t).getName().equals(name)) return t;
            if (t instanceof Film && ((Film) t).getName().equals(name)) return t;
        }
        return null;
    }

    static void check(String name, Object actual, Object expected) {
        caseNo++;
        boolean ok = Objects.equals(expected, actual);
        if (ok) pass++; else fail++;
        String line = String.format("[#%02d] %s\n      期望: %s   实测: %s   → %s",
                caseNo, name, expected, actual, ok ? "PASS" : "FAIL");
        System.out.println(line);
        if (!ok) {
            lastFailInfo = String.format("#%02d %s (期望 %s, 实测 %s)", caseNo, name, expected, actual);
            failures.add(lastFailInfo);
        }
    }

    static java.util.List<String> failures = new java.util.ArrayList<>();

    //================= 落盘旁证：直接反序列化服务端 data 目录 =================

    @SuppressWarnings("unchecked")
    static <T> List<T> readFile(String file) {
        try (ObjectInputStream ois = new ObjectInputStream(new java.io.FileInputStream(DATA_DIR + file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    static String userSummary() {
        List<User> all = readFile("user.obj");
        StringBuilder sb = new StringBuilder();
        for (User u : all) {
            sb.append(u.getUsername()).append("(state=").append(u.getState())
                    .append(u instanceof Seller ? ",seller" : "").append(") ");
        }
        return sb.toString().trim();
    }

    static String planSeatSummary(String planId) {
        List<FilmPlan> all = readFile("filmPlan.obj");
        for (FilmPlan p : all) {
            if (p.getId().equals(planId)) {
                return "计划" + planId + " 座位(0,0) owner=" + p.getHall().getSeats()[0][0].getOwner()
                        + " 余票=" + p.getHall().getRestTickets();
            }
        }
        return "计划" + planId + " 不存在";
    }

    static String orderSummary(String orderId) {
        List<Order> all = readFile("order.obj");
        for (Order o : all) {
            if (o.getId().equals(orderId)) {
                return "订单" + orderId + " state=" + o.getState() + " 座位=" + o.getRow() + "-" + o.getCol();
            }
        }
        return "订单" + orderId + " 不存在";
    }

    static void evidence(String file, String detail) {
        System.out.println("      [落盘旁证] " + file + " → " + detail);
    }
}
