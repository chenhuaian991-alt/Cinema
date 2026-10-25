package cinema.platform.server.service;

import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.Order;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单业务：在线订座、查看订单、修改订单、取消订单
 * <p>
 * 注意：座位是存在“影片计划里的影厅”中的，所以订座/退座要同时改动
 * 影片计划文件（座位图）和订单文件，两个文件要一起保存。
 */
public class OrderService {
    /**
     * 在线订座：占座 + 生成订单
     *
     * @param data id-影片计划编号，row-排号，col-列号，owner-订票用户账号
     * @return 1-成功，0-失败，-1-影片计划不存在，-2-座位已被订购
     */
    public static Integer orderSeatOnline(Map<String, Object> data) {
        String id = (String) data.get("id");
        Integer row = (Integer) data.get("row");
        Integer col = (Integer) data.get("col");
        String owner = (String) data.get("owner");
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        FilmPlan plan = null;
        for (FilmPlan p : plans) {
            if (p.getId().equals(id)) {
                plan = p;
                break;
            }
        }
        if(plan==null)return -1;
        FilmHall hall=plan.getHall();
        //行列必须落在座位图范围内，非法值按失败处理，不能让数组越界异常炸掉本次处理
        if(row==null||col==null||row<0||row>=hall.getTotalRow()||col<0||col>=hall.getTotalCol())return 0;
        //服务端必须再判一次，客户端看到的是旧座位图，别人可能已经抢先订了
        if(hall.hasOwner(row,col))return -2;
        hall.orderSeat(row,col,owner);

        Order order=new Order();
        order.setId(IdGenerator.generateId(10));
        order.setFilmPlanId(plan.getId());
        order.setFilmName(plan.getFilm().getName());
        order.setFilmHallName(hall.getName());
        order.setPlayDate(plan.getPlayDate());
        order.setBeginTime(plan.getBeginTime());
        order.setEndTime(plan.getEndTime());
        order.setRow(row);
        order.setCol(col);
        order.setUser(owner);
        order.setSeller(plan.getOwner());
        order.setState(1);

        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        orders.add(order);
        boolean success = FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE);
        if (success) {
            success = FileUtil.saveData(orders, FileUtil.ORDER_FILE);
        }
        return success ? 1 : 0;
    }

    /**
     * 查看订单：商家只看自己影院的，普通用户只看自己的
     *
     * @param user 登录者
     */
    public static List<Order> view(User user) {
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        List<Order> result = new ArrayList<>();
        for (Order o : orders) {
            if(user instanceof Seller){
                if (o.getSeller() != null && o.getSeller().getUsername().equals(user.getUsername())) {
                   result.add(o);
                }
            }else {
                if (o.getUser().equals(user.getUsername())) {
                    result.add(o);
                }
            }
        }
        return result;
    }

    /**
     * 修改订单：换座位（只能修改自己的订单）
     *
     * @param data id-订单编号，row-新排号，col-新列号，owner-用户账号
     * @return 1-成功，0-失败（含行列越界），-1-订单或影片计划不存在（含不是自己的订单），-2-新座位已被订购
     */
    public static Integer update(Map<String, Object> data) {
        String id = (String) data.get("id");
        Integer row = (Integer) data.get("row");
        Integer col = (Integer) data.get("col");
        String owner = (String) data.get("owner");
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        Order order = null;
        for (Order o : orders) {
            if (o.getId().equals(id) && o.getUser().equals(owner)) {
                order = o;
                break;
            }
        }
        if (order == null) {
            return -1;
        }
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        FilmPlan plan = null;
        for (FilmPlan p : plans) {
            if (p.getId().equals(order.getFilmPlanId())) {
                plan = p;
                break;
            }
        }
        if (plan == null) {
            return -1;
        }
        FilmHall hall=plan.getHall();
        //行列必须落在座位图范围内，非法值按失败处理，不能让数组越界异常炸掉本次处理
        if(row==null||col==null||row<0||row>=hall.getTotalRow()||col<0||col>=hall.getTotalCol())return 0;
        if(hall.hasOwner(row,col))return -2;
        hall.changeSeat(order.getRow(),order.getCol(),row,col);
        order.setRow(row);
        order.setCol(col);
        boolean success = FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE);
        if (success) {
            success = FileUtil.saveData(orders, FileUtil.ORDER_FILE);
        }
        return success ? 1 : 0;
    }

    /**
     * 取消订单：退座 + 订单状态改为已退订
     *
     * @param data id-订单编号，owner-用户账号（只能取消自己的订单）
     * @return 1-成功，0-失败，-1-订单不存在，-2-订单已经是退订状态
     */
    public static Integer cancel(Map<String, Object> data) {
        String id = (String) data.get("id");
        String owner = (String) data.get("owner");

        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        Order order = null;
        for (Order o : orders) {
            if (o.getId().equals(id) && o.getUser().equals(owner)) {
                order = o;
                break;
            }
        }
        if (order == null) {
            return -1;
        }
        if (order.getState()==0)return -2;
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        FilmPlan plan = null;
        for (FilmPlan p : plans) {
            if (p.getId().equals(order.getFilmPlanId())) {
                plan = p;
                break;
            }
        }
        if (plan == null) {
            return -1;
        }
        plan.getHall().cancelSeat(order.getRow(),order.getCol());
        order.setState(0);
        boolean success = FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE);
        if (success) {
            success = FileUtil.saveData(orders, FileUtil.ORDER_FILE);
        }
        return success ? 1 : 0;
    }

    /**
     * 通过编号查订单（修改订单前回显用）
     */
    public static Order getById(String id) {
        List<Order> orders = FileUtil.readData(FileUtil.ORDER_FILE);
        for (Order o : orders) {
            if (o.getId().equals(id)) {
                return o;
            }
        }
        return null;
    }

}
