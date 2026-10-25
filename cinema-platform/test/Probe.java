import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.message.Message;
import org.zlf.cinema.platfrom.util.SocketUtil;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订座落盘探针：订 (2,2) 后立刻反序列化 filmPlan.obj，验证座位 owner 是否真的写进文件。
 */
public class Probe {

    @SuppressWarnings("unchecked")
    static <T> List<T> read(String file) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                "E:/Java-demo/Cinema/cinema-platform/server-run/data/" + file))) {
            return (List<T>) ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 8888);
        Map<String, Object> data = new HashMap<>();
        data.put("id", "ZLF_AH698F7VPO");
        data.put("row", 2);
        data.put("col", 2);
        data.put("owner", "alice");
        SocketUtil.sendMsg(socket, new Message(Command.ORDER_SEAT_ONLINE, data));
        System.out.println("orderSeat(2,2) 接口返回 -> " + SocketUtil.receiveMsg(socket));
        socket.close();

        List<FilmPlan> plans = read("filmPlan.obj");
        for (FilmPlan p : plans) {
            System.out.println("PLAN " + p.getId()
                    + " seat(2,2).owner=" + p.getHall().getSeats()[2][2].getOwner()
                    + " rest=" + p.getHall().getRestTickets());
        }
    }
}
