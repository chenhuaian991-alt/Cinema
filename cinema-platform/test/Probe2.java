import cinema.platform.server.service.OrderService;
import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.Order;
import org.zlf.cinema.platfrom.message.Message;
import org.zlf.cinema.platfrom.util.SocketUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改订单探针：同一个 Map 先走 socket 调服务端，再绕过 socket 直接本地调
 * OrderService.update，用来区分「传输/分发问题」和「业务逻辑问题」。
 * 注意：直接本地调用时 FileUtil 按相对路径读 data/，所以必须在 server-run 目录下运行。
 */
public class Probe2 {

    @SuppressWarnings("unchecked")
    static List<Order> readOrders() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/order.obj"))) {
            return (List<Order>) ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        String orderId = "ZLF_VSOI9J5GNW";
        List<Order> orders = readOrders();
        for (Order o : orders) {
            System.out.println("文件里的订单: id=" + o.getId() + " user=" + o.getUser()
                    + " state=" + o.getState() + " seat=" + o.getRow() + "-" + o.getCol()
                    + " filmPlanId=" + o.getFilmPlanId());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", orderId);
        data.put("row", 1);
        data.put("col", 1);
        data.put("owner", "alice");

        Socket socket = new Socket("localhost", 8888);
        SocketUtil.sendMsg(socket, new Message(Command.UPDATE_ORDER, data));
        System.out.println("socket 调用 UPDATE_ORDER -> " + SocketUtil.receiveMsg(socket));
        socket.close();

        System.out.println("本地直接调 OrderService.update -> " + OrderService.update(data));
    }
}
