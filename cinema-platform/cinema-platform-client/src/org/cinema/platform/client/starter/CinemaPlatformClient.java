package org.cinema.platform.client.starter;

import org.cinema.platform.client.action.UserAction;
import org.cinema.platform.client.menu.Menu;
import org.cinema.platform.client.menu.MenuManager;
import org.cinema.platform.client.util.InputUtil;
import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.vo.LoginResult;

/**
 * 影院平台客户端
 */
public class CinemaPlatformClient {
    /**
     * 当前登录用户：登录成功后保存，商家相关操作要从中取账号
     */
    private static User currentUser;

    public static void main(String[] args) {
        showInterface(MenuManager.LOGIN_MENUS);
    }

    /**
     * 展示给定的菜单
     * @param menus
     */
    private static void showInterface(Menu[] menus){
        MenuManager.showMenu(menus);
        int number = InputUtil.getInputInteger("请选择菜单编号：", 1, menus.length);
        Menu select=menus[number-1];
        switch (select.getCommand()){
            //========== 登录界面 ==========
            case Command.REGISTER:
                UserAction.register();
                showInterface(menus);
                break;
            case Command.LOGIN:
                login(menus);
                break;
            case Command.ENTRY_APPLY:
                UserAction.entryApply();
                showInterface(menus);
                break;
            case Command.VIEW_ENTRY_APPLY:
                UserAction.viewEntryApply();
                showInterface(menus);
                break;
            case Command.GET_PASSWORD_BACK:
                UserAction.getPasswordBack();
                showInterface(menus);
                break;
            case Command.UNFROZEN_APPLY:
                UserAction.unfrozenApply();
                showInterface(menus);
                break;
            case Command.QUIT:
                System.out.println("感谢使用影院选票平台");
                System.exit(0);
                break;

            //========== 管理员 ==========
            case Command.VIEW_SELLERS:
                UserAction.viewSellers();
                showInterface(menus);
                break;
            case Command.AUDIT_SELLER:
                UserAction.auditSeller();
                showInterface(menus);
                break;
            case Command.FROZEN_SELLER:
                UserAction.frozenSeller();
                showInterface(menus);
                break;
            case Command.VIEW_USERS:
                UserAction.viewUsers();
                showInterface(menus);
                break;
            case Command.FROZEN_USER:
                UserAction.frozenUser();
                showInterface(menus);
                break;
            case Command.VIEW_UNFROZEN_APPLIES:
                UserAction.viewUnfrozenApplies();
                showInterface(menus);
                break;
            case Command.AUDIT_UNFROZEN_APPLY:
                UserAction.auditUnfrozenApply();
                showInterface(menus);
                break;

            //========== 商家：影厅管理 ==========
            case Command.VIEW_FILM_HALLS:
                UserAction.viewFilmHalls(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.ADD_FILM_HALL:
                UserAction.addFilmHall(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.UPDATE_FILM_HALL:
                UserAction.updateFilmHall(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.DELETE_FILM_HALL:
                UserAction.deleteFilmHall(currentUser.getUsername());
                showInterface(menus);
                break;

            //========== 商家：影片管理 ==========
            case Command.VIEW_FILMS:
                UserAction.viewFilms(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.ADD_FILM:
                UserAction.addFilm(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.UPDATE_FILM:
                UserAction.updateFilm(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.DELETE_FILM:
                UserAction.deleteFilm(currentUser.getUsername());
                showInterface(menus);
                break;

            //========== 商家：影片计划管理 ==========
            case Command.VIEW_FILM_PLANS:
                UserAction.viewSellerFilmPlans(currentUser);
                showInterface(menus);
                break;
            case Command.ADD_FILM_PLAN:
                UserAction.addFilmPlan((Seller) currentUser);
                showInterface(menus);
                break;
            case Command.UPDATE_FILM_PLAN:
                UserAction.updateFilmPlan((Seller) currentUser);
                showInterface(menus);
                break;
            case Command.DELETE_FILM_PLAN:
                UserAction.deleteFilmPlan((Seller) currentUser);
                showInterface(menus);
                break;

            //========== 订单 ==========
            case Command.VIEW_ORDERS:
                UserAction.viewOrders(currentUser);
                showInterface(menus);
                break;
            case Command.UPDATE_ORDER:
                UserAction.updateOrder(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.CANCEL_ORDER:
                UserAction.cancelOrder(currentUser.getUsername());
                showInterface(menus);
                break;
            case Command.ORDER_SEAT_ONLINE:
                UserAction.orderSeatOnline(currentUser.getUsername());
                showInterface(menus);
                break;

            //========== 菜单跳转 ==========
            case Command.SHOW_CHILDREN:
                showInterface(select.getChildren());
                break;
            case Command.GO_BACK_MAIN:
                showUserMenus();
                break;
            case Command.GO_BACK_LOGIN:
                currentUser = null;
                showInterface(MenuManager.LOGIN_MENUS);
                break;
            default:
                showInterface(menus);
                break;
        }
    }

    /**
     * 登录：根据 LoginResult 的标识给出提示，成功则进入对应身份的主菜单
     */
    private static void login(Menu[] menus) {
        LoginResult result=UserAction.login();
        if(result==null){
            System.out.println("服务器无响应，请检查服务端是否已启动");
            showInterface(menus);
            return;
        }
        switch (result.getCode()){
            case LoginResult.SUCCESS:
                currentUser = result.getUser();
                System.out.println("登录成功，欢迎您：" + currentUser.getUsername());
                showUserMenus();
                break;
            case LoginResult.AUDITING:
                System.out.println("账号正在审批中，请耐心等待");
                showInterface(menus);
                break;
            case LoginResult.FROZEN:
                System.out.println("账号已被冻结，请提交解冻申请");
                showInterface(menus);
                break;
            case LoginResult.REJECTED:
                System.out.println("账号审核未通过");
                showInterface(menus);
                break;
            case LoginResult.NOT_EXIST:
                System.out.println("账号不存在，请先注册");
                showInterface(menus);
                break;
            default:
                System.out.println("账号或密码错误，请重新登录");
                showInterface(menus);
                break;
        }
    }


    /**
     * 按登录者的角色展示对应的主菜单
     */
    private static void showUserMenus(){
        User.Role role=currentUser.getRole();
        if (role == User.Role.MANAGER) {
            showInterface(MenuManager.MANAGER_MENUS);
        } else if (role == User.Role.SELLER) {
            showInterface(MenuManager.SELLER_MENUS);
        } else {
            showInterface(MenuManager.USER_MENUS);
        }
    }

//    private static void showLoginMenu(){
//        MenuManager.showMenu(MenuManager.LOGIN_MENUS);
//        int number= InputUtil.getInputInteger("请选择菜单编号：",1,MenuManager.LOGIN_MENUS.length);
//        Menu select=MenuManager.LOGIN_MENUS[number-1];
//        switch (select.getCommand()){
//            case Command.REGISTER:
//                System.out.println("你选择了注册");
//
//        }
//    }
}
