package cinema.platform.server.task;

import cinema.platform.server.service.AdminService;
import cinema.platform.server.service.FilmHallService;
import cinema.platform.server.service.FilmPlanService;
import cinema.platform.server.service.FilmService;
import cinema.platform.server.service.OrderService;
import cinema.platform.server.service.UserService;
import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.Film;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.entity.UnfrozenApply;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.message.Message;
import org.zlf.cinema.platfrom.util.SocketUtil;

import java.net.Socket;
import java.util.Map;

/**
 * 消息处理任务
 * <p>
 * 一个客户端连接对应一个任务、一个线程。
 * 这一层只做三件事：收消息 → 看命令 → 交给对应的业务方法，
 * 最后把业务方法返回的结果原样发回客户端。
 * 真正的处理逻辑都在 service 包里，这样任务类不会越写越长。
 */
public class MessageProcessTask implements Runnable{
    private Socket socket;

    public MessageProcessTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Message message=SocketUtil.receiveMsg(socket);
            if(message==null){
                return;
            }
            Object data=message.getData();
            Object result=null;
            switch (message.getCommand()){
                //========== 登录界面 ==========
                case Command.REGISTER://注册
                case Command.ENTRY_APPLY://入驻申请（同样是注册，数据是 Seller）
                    result = UserService.register(data);
                    break;
                case Command.LOGIN://登录
                    result=UserService.login((User) data);
                    break;
                case Command.VIEW_ENTRY_APPLY://查看入驻申请
                    result = AdminService.viewEntryApply((String) data);
                    break;
                case Command.GET_PASSWORD_BACK://找回密码
                    result = UserService.getPasswordBack((User) data);
                    break;
                case Command.UNFROZEN_APPLY://申请解冻
                    result = UserService.unfrozenApply((UnfrozenApply) data);
                    break;

                //========== 管理员主界面 ==========
                case Command.VIEW_SELLERS://查看商家
                    result = AdminService.viewSellers((String) data);
                    break;
                case Command.AUDIT_SELLER://审核商家
                    result = AdminService.auditSeller(map(data));
                    break;
                case Command.FROZEN_SELLER://冻结商家
                    result = AdminService.frozenSeller((String) data);
                    break;
                case Command.VIEW_USERS://查看用户
                    result = AdminService.viewUsers();
                    break;
                case Command.FROZEN_USER://冻结用户
                    result = AdminService.frozenUser((String) data);
                    break;
                case Command.VIEW_UNFROZEN_APPLIES://查看解冻申请
                    result = AdminService.viewUnfrozenApplies();
                    break;
                case Command.AUDIT_UNFROZEN_APPLY://审批解冻申请
                    result = AdminService.auditUnfrozenApply(map(data));
                    break;

                //========== 商家主界面：影厅管理 ==========
                case Command.VIEW_FILM_HALLS://查看影厅
                    result = FilmHallService.view((String) data);
                    break;
                case Command.ADD_FILM_HALL://增加影厅
                    result = FilmHallService.add((FilmHall) data);
                    break;
                case Command.UPDATE_FILM_HALL://修改影厅
                    result = FilmHallService.update((FilmHall) data);
                    break;
                case Command.DELETE_FILM_HALL://删除影厅
                    result = FilmHallService.delete(map(data));
                    break;

                //========== 商家主界面：影片管理 ==========
                case Command.VIEW_FILMS://查看影片
                    result = FilmService.view(map(data));
                    break;
                case Command.ADD_FILM://增加影片
                    result = FilmService.add((Film) data);
                    break;
                case Command.UPDATE_FILM://修改影片
                    result = FilmService.update((Film) data);
                    break;
                case Command.DELETE_FILM://删除影片
                    result = FilmService.delete(map(data));
                    break;

                //========== 商家/用户：影片计划 ==========
                case Command.VIEW_FILM_PLANS://查看影片计划
                    result = FilmPlanService.view(map(data));
                    break;
                case Command.ADD_FILM_PLAN://增加影片计划
                    result = FilmPlanService.add((FilmPlan) data);
                    break;
                case Command.UPDATE_FILM_PLAN://修改影片计划
                    result = FilmPlanService.update(map(data));
                    break;
                case Command.DELETE_FILM_PLAN://删除影片计划
                    result = FilmPlanService.delete(map(data));
                    break;
                case Command.GET_FILM_PLAN_BY_ID://按编号查影片计划
                    result = FilmPlanService.getById((String) data);
                    break;

                //========== 商家/用户：订单 ==========
                case Command.ORDER_SEAT_ONLINE://在线订座
                    result = OrderService.orderSeatOnline(map(data));
                    break;
                case Command.VIEW_ORDERS://查看订单
                    result = OrderService.view((User) data);
                    break;
                case Command.UPDATE_ORDER://修改订单
                    result = OrderService.update(map(data));
                    break;
                case Command.CANCEL_ORDER://取消订单
                    result = OrderService.cancel(map(data));
                    break;
                case Command.GET_ORDER_BY_ID://按编号查订单
                    result = OrderService.getById((String) data);
                    break;
                default:
                    //SHOW_CHILDREN、QUIT 这类命令只在客户端内部使用，不会发到服务器
                    result = null;
                    break;
            }
            //4.把处理结果发回客户端
            SocketUtil.sendMsg(socket,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Message 的泛型在接收端会被擦除，这里统一转成 Map 使用
     */
    @SuppressWarnings("unchecked")
    private static Map<String,Object>map(Object data){
        return (Map<String, Object>) data;
    }
}
