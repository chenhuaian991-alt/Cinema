import org.zlf.cinema.platfrom.entity.Film;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.Order;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 联调辅助：把服务端 data 目录里各存档的编号打印出来，
 * 供脚本化客户端回放在会话之间取动态 id 用。
 */
public class DumpIds {

    @SuppressWarnings("unchecked")
    static <T> List<T> read(String file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                "E:/Java-demo/Cinema/cinema-platform/server-run/data/" + file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        List<Film> films = read("film.obj");
        for (Film f : films) {
            System.out.println("FILM " + f.getId() + " " + f.getName());
        }
        List<FilmHall> halls = read("filmHall.obj");
        for (FilmHall h : halls) {
            System.out.println("HALL " + h.getId() + " " + h.getName());
        }
        List<FilmPlan> plans = read("filmPlan.obj");
        for (FilmPlan p : plans) {
            System.out.println("PLAN " + p.getId() + " " + p.getHall().getName() + " " + p.getBeginTime()
                    + " seat00=" + p.getHall().getSeats()[0][0].getOwner()
                    + " rest=" + p.getHall().getRestTickets());
        }
        List<Order> orders = read("order.obj");
        for (Order o : orders) {
            System.out.println("ORDER " + o.getId() + " state=" + o.getState() + " seat=" + o.getRow() + "-" + o.getCol());
        }
    }
}
