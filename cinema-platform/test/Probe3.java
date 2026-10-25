import cinema.platform.server.service.OrderService;
import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.Order;
import org.zlf.cinema.platfrom.message.Message;
import org.zlf.cinema.platfrom.util.SocketUtil;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改订单失败定位探针：
 * 1.走 socket 订一个座（造出「订单 state=1 + 座位已占」的失败同款状态）；
 * 2.直接调用 OrderService.update 复现 -1；
 * 3.手动执行 update 内部的每个判断，打印到底哪一步不成立。
 * 必须在 server-run 目录下运行（FileUtil 用相对路径）。
 */
public class Probe3 {

    static final String PLAN_ID = "ZLF_JDLXMMYI50";

    public static void main(String[] args) throws Exception {
        //1.socket 订座 (0,0)
        Socket socket = new Socket("localhost", 8888);
        Map<String, Object> book = new HashMap<>();
        book.put("id", PLAN_ID);
        book.put("row", 0);
        book.put("col", 0);
        book.put("owner", "alice");
        SocketUtil.sendMsg(socket, new Message(Command.ORDER_SEAT_ONLINE, book));
        System.out.println("订座 -> " + SocketUtil.receiveMsg(socket));
        socket.close();

        //2.从文件里找刚生成的订单（state=1 的那条）
        String orderId = null;
        for (Object o : FileUtil.readData(FileUtil.ORDER_FILE)) {
            Order ord = (Order) o;
            if (ord.getState() == 1 && PLAN_ID.equals(ord.getFilmPlanId())) orderId = ord.getId();
        }
        System.out.println("新订单 id = " + orderId);

        //3.直接调 update 复现
        Map<String, Object> data = new HashMap<>();
        data.put("id", orderId);
        data.put("row", 1);
        data.put("col", 1);
        data.put("owner", "alice");
        System.out.println("OrderService.update -> " + OrderService.update(data));

        //4.手动重放 update 内部的判断（注意：上面 update 可能已改状态，这里重新读文件看静态条件）
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        for (Order o : orders) {
            if (orderId.equals(o.getId())) {
                System.out.println("订单字段: user=[" + o.getUser() + "] state=" + o.getState()
                        + " row=" + o.getRow() + " col=" + o.getCol() + " filmPlanId=" + o.getFilmPlanId());
                System.out.println("判断 id 相等   -> " + o.getId().equals(orderId));
                System.out.println("判断 user 匹配 -> " + (o.getUser() != null && o.getUser().equals("alice")));
            }
        }
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        boolean planFound = false;
        for (FilmPlan p : plans) {
            if (PLAN_ID.equals(p.getId())) {
                planFound = true;
                System.out.println("计划找到: " + p.getId() + " seat(0,0)=" + p.getHall().getSeats()[0][0].getOwner()
                        + " seat(1,1)=" + p.getHall().getSeats()[1][1].getOwner()
                        + " rest=" + p.getHall().getRestTickets());
            }
        }
        System.out.println("计划存在 -> " + planFound);
    }
}
