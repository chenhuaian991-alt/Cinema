package org.cinema.platform.client.action;

import org.cinema.platform.client.interact.MessageSender;
import org.cinema.platform.client.util.InputUtil;
import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.Film;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.Order;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.UnfrozenApply;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.vo.LoginResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为
 * <p>
 * 客户端的所有动作都遵循同一个套路：
 * 1.用 InputUtil 从控制台收集数据；
 * 2.把数据装进命令，用 MessageSender 发给服务器；
 * 3.根据服务器返回的标识，打印对应的提示。
 */
public class UserAction {
    /**
     * 注册
     */
    public static void register(){
        String username = InputUtil.getInputText("请输入账号：");
        String password = InputUtil.getInputText("请输入密码：");
        String securityCode = InputUtil.getInputText("请输入安全码：");
        User user = new User(username, password, securityCode);
        Integer result=MessageSender.sendMsg(Command.REGISTER,user);
        if (result == null || result == 0) {
            System.out.println("注册失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("注册成功");
        } else {
            System.out.println("账号已被注册");
        }
    }
    /**
     * 登录：把服务器返回的 LoginResult 交给启动类去判断并展示主菜单
     */
    public static LoginResult login(){
        String username = InputUtil.getInputText("请输入账号：");
        String password = InputUtil.getInputText("请输入密码：");
        User user = new User(username, password, null);
        return MessageSender.sendMsg(Command.LOGIN, user);
    }
    /**
     * 入驻申请
     */
    public static void entryApply(){
        String username = InputUtil.getInputText("请输入账号：");
        String password = InputUtil.getInputText("请输入密码：");
        String securityCode = InputUtil.getInputText("请输入安全码：");
        String cinemaName = InputUtil.getInputText("请输入影院名称：");
        String cinemaAddress = InputUtil.getInputText("请输入影院地址：");
        Seller seller = new Seller(username, password, securityCode, cinemaAddress, cinemaName);
        Integer result = MessageSender.sendMsg(Command.ENTRY_APPLY, seller);
        if (result == null || result == 0) {
            System.out.println("入驻申请失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("入驻申请成功，请等待平台审核");
        } else {
            System.out.println("账号或影院名称已被使用");
        }
    }
    /**
     * 查案入驻申请
     */
    public static void viewEntryApply(){
        String cinemaName = InputUtil.getInputText("请输入影院名称：（Y-查看所有）");
        List<Seller> sellers = MessageSender.sendMsg(Command.VIEW_ENTRY_APPLY, cinemaName);
        if (sellers == null || sellers.isEmpty()) {
            System.out.println("未找到与\"" + cinemaName + "\"相关的入驻申请");
        } else {
            System.out.println("账号\t影院名称\t影院地址\t角色\t状态");
            for (Seller seller : sellers) {
                System.out.println(seller);
            }
        }
    }
    /**
     * 找回密码
     */
    public static void getPasswordBack(){
        String username = InputUtil.getInputText("请输入账号：");
        String securityCode = InputUtil.getInputText("请输入安全码：");
        User user = new User(username, null, securityCode);
        String password = MessageSender.sendMsg(Command.GET_PASSWORD_BACK, user);
        if (password == null) {
            System.out.println("账号不存在或安全码不正确");
        } else {
            System.out.println("您的密码是：" + password);
        }
    }
    /**
     * 解冻申请
     */
    public static void unfrozenApply(){
        String username = InputUtil.getInputText("请输入账号：");
        String reason = InputUtil.getInputText("请输入解冻理由：");
        UnfrozenApply apply = new UnfrozenApply(username, reason);
        Integer result = MessageSender.sendMsg(Command.UNFROZEN_APPLY, apply);
        if (result == null || result == 0) {
            System.out.println("解冻申请失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("解冻申请已提交，请等待审批");
        } else if (result == -1) {
            System.out.println("解冻账号不存在");
        } else if (result == -2) {
            System.out.println("账号未被冻结，不能进行解冻申请");
        } else {
            System.out.println("该账号存在未处理的解冻申请，请勿重复提交");
        }
    }
    /**
     * 查看商家
     */
    public static void viewSellers(){
        String cinemaName = InputUtil.getInputText("请输入影院名称：（Y-查看所有）");
        List<Seller> sellers = MessageSender.sendMsg(Command.VIEW_SELLERS, cinemaName);
        if (sellers == null || sellers.isEmpty()) {
            System.out.println("未找到与\"" + cinemaName + "\"相关的商家信息");
        } else {
            System.out.println("账号\t影院名称\t影院地址\t角色\t状态");
            for (Seller seller : sellers) {
                System.out.println(seller);
            }
        }
    }
    /**
     * 审核商家
     */
    public static void auditSeller(){
        String username = InputUtil.getInputText("请输入商家账号：");
        int state = InputUtil.getInputInteger("请输入审核结果：（-1-审核不通过，1-审核通过）", -1, 1);
        Map<String,Object>data=new HashMap<>();
        data.put("username",username);
        data.put("state",state);
        Integer result = MessageSender.sendMsg(Command.AUDIT_SELLER, data);
        if (result == null || result == 0) {
            System.out.println("审核失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("审核成功");
        } else if (result == -1) {
            System.out.println("商家不存在");
        } else {
            System.out.println("该商家不处于待审核状态，不能审核");
        }
    }
    /**
     * 冻结商家
     */
    public static void frozenSeller(){
        String username = InputUtil.getInputText("请输入商家账号：");
        Integer result = MessageSender.sendMsg(Command.FROZEN_SELLER, username);
        if (result == null || result == 0) {
            System.out.println("冻结失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("冻结成功");
        } else if (result == -1) {
            System.out.println("商家不存在");
        } else {
            System.out.println("该商家不处于正常状态，不能冻结");
        }
    }
    /**
     * 查看用户
     */
    public static void viewUsers(){
        List<User> users = MessageSender.sendMsg(Command.VIEW_USERS, null);
        if (users == null || users.isEmpty()) {
            System.out.println("当前暂无用户信息");
        } else {
            System.out.println("账号\t角色\t\t状态");
            for (User user : users) {
                System.out.println(user);
            }
        }
    }
    /**
     * 冻结用户
     */
    public static void frozenUser(){
        String username = InputUtil.getInputText("请输入用户账号：");
        Integer result = MessageSender.sendMsg(Command.FROZEN_USER, username);
        if (result == null || result == 0) {
            System.out.println("冻结失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("冻结成功");
        } else if (result == -1) {
            System.out.println("用户不存在");
        } else {
            System.out.println("该用户不处于正常状态，不能冻结");
        }
    }
    /**
     * 查看解冻申请
     */
    public static void viewUnfrozenApplies(){
        List<UnfrozenApply> applies = MessageSender.sendMsg(Command.VIEW_UNFROZEN_APPLIES, null);
        if (applies == null || applies.isEmpty()) {
            System.out.println("当前暂无解冻申请");
        } else {
            System.out.println("编号\t\t\t\t账号\t理由\t处理状态");
            for (UnfrozenApply apply : applies) {
                System.out.println(apply);
            }
        }
    }
    /**
     * 审批解冻申请
     */
    public static void auditUnfrozenApply(){
        String id = InputUtil.getInputText("请输入解冻申请编号：");
        int state = InputUtil.getInputInteger("请输入审批结果：（1-通过，2-驳回）", 1, 2);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("state", state);
        Integer result = MessageSender.sendMsg(Command.AUDIT_UNFROZEN_APPLY, data);
        if (result == null || result == 0) {
            System.out.println("审批失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("审批成功");
        } else if (result == -1) {
            System.out.println("解冻申请不存在");
        } else {
            System.out.println("该申请不处于待处理状态，不能审批");
        }
    }
    /**
     * 查看影厅
     */
    public static void viewFilmHalls(String owner){
        List<FilmHall> halls = MessageSender.sendMsg(Command.VIEW_FILM_HALLS, owner);
        if (halls == null || halls.isEmpty()) {
            System.out.println("当前暂无影厅信息");
        } else {
            System.out.println("编号\t\t\t\t名称\t总排数\t总列数");
            for (FilmHall hall : halls) {
                System.out.println(hall);
            }
        }
    }
    /**
     * 添加影厅
     */
    public static void addFilmHall(String owner){
        String name = InputUtil.getInputText("请输入影厅名称：");
        int totalRow = InputUtil.getInputInteger("请输入影厅总排数：", 5, 10);
        int totalCol = InputUtil.getInputInteger("请输入影厅总列数：", 5, 10);
        FilmHall hall = new FilmHall(name, totalRow, totalCol, owner);
        Integer result = MessageSender.sendMsg(Command.ADD_FILM_HALL, hall);
        if (result == null || result == 0) {
            System.out.println("增加影厅失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("增加影厅成功");
        } else {
            System.out.println("已存在同名影厅，请勿重复添加");
        }
    }
    /**
     * 修改影厅
     */
    public static void updateFilmHall(String owner){
        String id = InputUtil.getInputText("请输入影厅编号：");
        String name = InputUtil.getInputText("请输入影厅名称：");
        int totalRow = InputUtil.getInputInteger("请输入影厅总排数：", 5, 10);
        int totalCol = InputUtil.getInputInteger("请输入影厅总列数：", 5, 10);
        FilmHall hall = new FilmHall(name, totalRow, totalCol, owner);
        hall.setId(id);
        Integer result = MessageSender.sendMsg(Command.UPDATE_FILM_HALL, hall);
        if (result == null || result == 0) {
            System.out.println("修改影厅失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("修改影厅成功");
        } else {
            System.out.println("未找到与\"" + id + "\"相关的影厅");
        }
    }
    /**
     * 删除影厅
     */
    public static void deleteFilmHall(String owner){
        String id = InputUtil.getInputText("请输入影厅编号：");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("owner", owner);
        Integer result = MessageSender.sendMsg(Command.DELETE_FILM_HALL, data);
        if (result == null || result == 0) {
            System.out.println("删除影厅失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("删除影厅成功");
        } else {
            System.out.println("未找到与\"" + id + "\"相关的影厅");
        }
    }
    /**
     * 查看影片
     */
    public static void viewFilms(String owner){
        String name = InputUtil.getInputText("请输入影片名称：（Y-查看所有）");
        Map<String, Object> data = new HashMap<>();
        data.put("owner", owner);
        data.put("name", name);
        List<Film> films = MessageSender.sendMsg(Command.VIEW_FILMS, data);
        if (films == null || films.isEmpty()) {
            System.out.println("未找到与\"" + name + "\"相关的影片");
        } else {
            System.out.println("编号\t\t\t\t名称\t主演\t描述");
            for (Film film : films) {
                System.out.println(film);
            }
        }
    }
    /**
     * 添加影片
     */
    public static void addFilm(String owner){
        String name = InputUtil.getInputText("请输入影片名称：");
        String actor = InputUtil.getInputText("请输入影片主演：");
        String description = InputUtil.getInputText("请输入影片描述：");
        Film film = new Film(name, actor, description, owner);
        Integer result = MessageSender.sendMsg(Command.ADD_FILM, film);
        if (result == null || result == 0) {
            System.out.println("增加影片失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("增加影片成功");
        } else {
            System.out.println("同名同主演的影片已存在，请勿重复添加");
        }
    }
    /**
     * 修改影片
     */
    public static void updateFilm(String owner){
        String id = InputUtil.getInputText("请输入影片编号：");
        String name = InputUtil.getInputText("请输入影片名称：");
        String actor = InputUtil.getInputText("请输入影片主演：");
        String description = InputUtil.getInputText("请输入影片描述：");
        Film film = new Film(name, actor, description, owner);
        film.setId(id);
        Integer result = MessageSender.sendMsg(Command.UPDATE_FILM, film);
        if (result == null || result == 0) {
            System.out.println("修改影片失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("修改影片成功");
        } else {
            System.out.println("未找到与\"" + id + "\"相关的影片");
        }
    }
    /**
     * 删除影片
     */
    public static void deleteFilm(String owner) {
        String id = InputUtil.getInputText("请输入影片编号：");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("owner", owner);
        Integer result = MessageSender.sendMsg(Command.DELETE_FILM, data);
        if (result == null || result == 0) {
            System.out.println("删除影片失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("删除影片成功");
        } else {
            System.out.println("未找到与\"" + id + "\"相关的影片");
        }
    }
    /**
     * 查看影片计划：商家只看自己的，普通用户看所有商家的
     */
    public static void viewSellerFilmPlans(User user){
        String filmName = InputUtil.getInputText("请输入影片名称：（Y-查看所有）");
        Map<String, Object> data = new HashMap<>();
        data.put("filmName", filmName);
        if (user instanceof Seller) {
            data.put("owner", user.getUsername());
        }
        List<FilmPlan> plans = MessageSender.sendMsg(Command.VIEW_FILM_PLANS, data);
        if (plans == null || plans.isEmpty()) {
            System.out.println("当前暂无影片计划");
        } else {
            System.out.println("编号\t\t\t\t影片\t影厅\t播放日期\t时间\t\t余票\t影院");
            for (FilmPlan plan : plans) {
                System.out.println(plan);
            }
        }
    }
    /**
     * 增加影片计划：先选影片，再选影厅，最后填日期时间
     */
    public static void addFilmPlan(Seller owner){
        //1.选影片
        Map<String, Object> filmData = new HashMap<>();
        filmData.put("owner", owner.getUsername());
        filmData.put("name", "y");
        List<Film> films = MessageSender.sendMsg(Command.VIEW_FILMS, filmData);
        if (films == null || films.isEmpty()) {
            System.out.println("请先添加影片");
            return;
        }
        System.out.println("编号\t\t\t\t名称\t主演\t描述");
        for (Film film : films) {
            System.out.println(film);
        }

        Film selectFilm = null;
        while (selectFilm == null) {
            String filmId = InputUtil.getInputText("请输入影片编号：");
            for (Film film : films) {
                if (film.getId().equals(filmId)) {
                    selectFilm = film;
                    break;
                }
            }
            if (selectFilm == null) {
                System.out.println("影片编号输入错误");
            }
        }

        //2.选影厅
        List<FilmHall> halls = MessageSender.sendMsg(Command.VIEW_FILM_HALLS, owner.getUsername());
        if (halls == null || halls.isEmpty()) {
            System.out.println("请先添加影厅");
            return;
        }
        System.out.println("编号\t\t\t\t名称\t总排数\t总列数");
        for (FilmHall hall : halls) {
            System.out.println(hall);
        }
        FilmHall selectHall = null;
        while (selectHall == null) {
            String hallId = InputUtil.getInputText("请输入影厅编号：");
            for (FilmHall hall : halls) {
                if (hall.getId().equals(hallId)) {
                    selectHall = hall;
                    break;
                }
            }
            if (selectHall == null) {
                System.out.println("影厅编号输入错误");
            }
        }

        //3.填日期时间
        String playDate = InputUtil.getInputDate("请输入播放日期（yyyy-MM-dd）：", "yyyy-MM-dd");
        String beginTime = InputUtil.getInputDate("请输入开始时间（HH:mm:ss）：", "HH:mm:ss");
        String endTime = InputUtil.getInputDate("请输入结束时间（HH:mm:ss）：", "HH:mm:ss");
        FilmPlan plan = new FilmPlan(selectFilm, selectHall, playDate, beginTime, endTime, owner);
        Integer result = MessageSender.sendMsg(Command.ADD_FILM_PLAN, plan);
        if (result == null || result == 0) {
            System.out.println("增加影片计划失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("增加影片计划成功");
        } else {
            System.out.println("该影厅在这个时间段已有排片，时间冲突");
        }
    }
    /**
     * 修改影片计划
     */
    public static void updateFilmPlan(Seller owner){
        String id = InputUtil.getInputText("请输入影片计划编号：");
        String playDate = InputUtil.getInputDate("请输入播放日期（yyyy-MM-dd）：", "yyyy-MM-dd");
        String beginTime = InputUtil.getInputDate("请输入开始时间（HH:mm:ss）：", "HH:mm:ss");
        String endTime = InputUtil.getInputDate("请输入结束时间（HH:mm:ss）：", "HH:mm:ss");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("date", playDate);
        data.put("beginTime", beginTime);
        data.put("endTime", endTime);
        data.put("owner", owner.getUsername());
        Integer result = MessageSender.sendMsg(Command.UPDATE_FILM_PLAN, data);
        if (result == null || result == 0) {
            System.out.println("修改影片计划失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("修改影片计划成功");
        } else if (result == -1) {
            System.out.println("未找到与\"" + id + "\"相关的影片计划");
        } else {
            System.out.println("修改后的时间与已有排片冲突");
        }
    }
    /**
     * 删除影片计划
     */
    public static void deleteFilmPlan(Seller owner){
        String id = InputUtil.getInputText("请输入影片计划编号：");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("owner", owner.getUsername());
        Integer result = MessageSender.sendMsg(Command.DELETE_FILM_PLAN, data);
        if (result == null || result == 0) {
            System.out.println("删除影片计划失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("删除影片计划成功");
        } else {
            System.out.println("未找到与\"" + id + "\"相关的影片计划");
        }
    }
    /**
     * 查看订单：商家看自己影院的，普通用户看自己的
     */
    public static void viewOrders(User user){
        List<Order> orders = MessageSender.sendMsg(Command.VIEW_ORDERS, user);
        if (orders == null || orders.isEmpty()) {
            System.out.println("当前暂无订单");
        } else {
            System.out.println("编号\t\t\t\t影院\t影片\t影厅\t座位\t日期\t时间\t\t地址\t状态");
            for (Order order : orders) {
                System.out.println(order);
            }
        }
    }

    /**
     * 在线订座
     */
    public static void orderSeatOnline(String owner) {
        String id = InputUtil.getInputText("请输入影片计划编号：");
        FilmPlan plan = MessageSender.sendMsg(Command.GET_FILM_PLAN_BY_ID, id);
        if (plan == null) {
            System.out.println("未找到与\"" + id + "\"相关的影片计划");
            return;
        }
        FilmHall hall = plan.getHall();
        hall.showSeatInfo();
        while (true) {
            //排号、列号从 0 开始，最大是总排数/总列数 - 1
            int row = InputUtil.getInputInteger("请输入排号：", 0, hall.getTotalRow() - 1);
            int col = InputUtil.getInputInteger("请输入列号：", 0, hall.getTotalCol() - 1);
            if (hall.hasOwner(row, col)) {
                System.out.println("该座位已被订购，请重新选择");
                continue;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("row", row);
            data.put("col", col);
            data.put("owner", owner);
            Integer result = MessageSender.sendMsg(Command.ORDER_SEAT_ONLINE, data);
            if (result == null || result == 0) {
                System.out.println("订座失败，请稍后重试");
            } else if (result == 1) {
                System.out.println("订座成功");
            } else if (result == -1) {
                System.out.println("影片计划不存在");
            } else {
                System.out.println("该座位已被他人订购，请重新选择");
                continue;
            }
            break;
        }
    }
    /**
     * 修改订单
     */
    public static void updateOrder(String owner){
        String id = InputUtil.getInputText("请输入订单编号：");
        Order order = MessageSender.sendMsg(Command.GET_ORDER_BY_ID, id);
        if (order == null) {
            System.out.println("未找到与\"" + id + "\"相关的订单");
            return;
        }
        if (!owner.equals(order.getUser())) {
            System.out.println("只能修改自己的订单");
            return;
        }
        FilmPlan plan = MessageSender.sendMsg(Command.GET_FILM_PLAN_BY_ID, order.getFilmPlanId());
        if (plan == null) {
            System.out.println("订单对应的影片计划已不存在");
            return;
        }
        FilmHall hall = plan.getHall();
        hall.showSeatInfo();
        while(true){
            int row = InputUtil.getInputInteger("请输入新排号：", 0, hall.getTotalRow() - 1);
            int col = InputUtil.getInputInteger("请输入新列号：", 0, hall.getTotalCol() - 1);
            if (hall.hasOwner(row, col)) {
                System.out.println("该座位已被订购，请重新选择");
                continue;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            data.put("row", row);
            data.put("col", col);
            data.put("owner",owner);
            Integer result = MessageSender.sendMsg(Command.UPDATE_ORDER, data);
            if (result == null || result == 0) {
                System.out.println("修改订单失败，请稍后重试");
            } else if (result == 1) {
                System.out.println("修改订单成功");
            } else if (result == -1) {
                System.out.println("订单或影片计划不存在");
            } else {
                System.out.println("该座位已被他人订购，请重新选择");
                continue;
            }
            break;
        }
    }
    /**
     * 取消订单
     */
    public static void cancelOrder(String owner){
        String id = InputUtil.getInputText("请输入订单编号：");
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("owner", owner);
        Integer result = MessageSender.sendMsg(Command.CANCEL_ORDER, data);
        if (result == null || result == 0) {
            System.out.println("取消订单失败，请稍后重试");
        } else if (result == 1) {
            System.out.println("取消订单成功");
        } else if (result == -1) {
            System.out.println("未找到与\"" + id + "\"相关的订单");
        } else {
            System.out.println("该订单已经是退订状态，不能重复取消");
        }
    }
}
