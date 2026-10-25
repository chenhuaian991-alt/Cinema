package org.cinema.platform.client.menu;

import org.zlf.cinema.platfrom.command.Command;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * 菜单管理器
 */
public class MenuManager {
    /**
     * 登录菜单
     */
    public static final Menu[] LOGIN_MENUS={
            new Menu(1, "注册", Command.REGISTER),
            new Menu(2, "登录", Command.LOGIN),
            new Menu(3, "入驻申请", Command.ENTRY_APPLY),
            new Menu(4, "查看入驻申请", Command.VIEW_ENTRY_APPLY),
            new Menu(5, "找回密码", Command.GET_PASSWORD_BACK),
            new Menu(6, "申请解冻", Command.UNFROZEN_APPLY),
            new Menu(7, "退出", Command.QUIT),

    };
    /**
     * 用户菜单
     */
    public static final Menu[] USER_MENUS;
    static {
            Menu menu1 = new Menu(1, "我的订单", Command.SHOW_CHILDREN);
            menu1.addChild(new Menu(1, "查看订单", Command.VIEW_ORDERS));
            menu1.addChild(new Menu(2, "修改订单", Command.UPDATE_ORDER));
            menu1.addChild(new Menu(3, "取消订单", Command.CANCEL_ORDER));
            menu1.addChild(new Menu(4, "返回主菜单", Command.GO_BACK_MAIN));

            Menu menu2 = new Menu(2, "购买影票", Command.SHOW_CHILDREN);
            menu2.addChild(new Menu(1, "查看影片计划",
                    Command.VIEW_FILM_PLANS));
            menu2.addChild(new Menu(2, "在线订座",
                    Command.ORDER_SEAT_ONLINE));
            menu2.addChild(new Menu(3, "返回主菜单", Command.GO_BACK_MAIN));

            Menu menu3 = new Menu(3, "返回登录", Command.GO_BACK_LOGIN);

            USER_MENUS=new Menu[]{menu1,menu2,menu3};
    }
    /**
     * 管理员菜单
     */
    public static final Menu[] MANAGER_MENUS ;
    static {
        Menu menu1 = new Menu(1, "商家管理", Command.SHOW_CHILDREN);
        menu1.addChild(new Menu(1, "查看商家", Command.VIEW_SELLERS));
        menu1.addChild(new Menu(2, "审核商家", Command.AUDIT_SELLER));
        menu1.addChild(new Menu(3, "冻结商家", Command.FROZEN_SELLER));
        menu1.addChild(new Menu(4, "返回主菜单", Command.GO_BACK_MAIN));

        Menu menu2 = new Menu(2, "用户管理", Command.SHOW_CHILDREN);
        menu2.addChild(new Menu(1, "查看用户", Command.VIEW_USERS));
        menu2.addChild(new Menu(2, "冻结用户", Command.FROZEN_USER));
        menu2.addChild(new Menu(3, "返回主菜单", Command.GO_BACK_MAIN));

        Menu menu3 = new Menu(3, "解冻申请管理", Command.SHOW_CHILDREN);
        menu3.addChild(new Menu(1, "查看解冻申请",
                Command.VIEW_UNFROZEN_APPLIES));
        menu3.addChild(new Menu(2, "审批解冻申请",
                Command.AUDIT_UNFROZEN_APPLY));
        menu3.addChild(new Menu(3, "返回主菜单", Command.GO_BACK_MAIN));
        Menu menu4 = new Menu(4, "返回登录", Command.GO_BACK_LOGIN);

        MANAGER_MENUS=new Menu[]{menu1,menu2,menu3,menu4};
    }
    /**
     * 商家菜单
     */
    public static final Menu[] SELLER_MENUS;
    static {
        Menu menu1 = new Menu(1, "影厅管理", Command.SHOW_CHILDREN);
        menu1.addChild(new Menu(1, "查看影厅", Command.VIEW_FILM_HALLS));
        menu1.addChild(new Menu(2, "增加影厅", Command.ADD_FILM_HALL));
        menu1.addChild(new Menu(3, "修改影厅", Command.UPDATE_FILM_HALL));
        menu1.addChild(new Menu(4, "删除影厅", Command.DELETE_FILM_HALL));
        menu1.addChild(new Menu(5, "返回主菜单", Command.GO_BACK_MAIN));

        Menu menu2 = new Menu(2, "影片管理", Command.SHOW_CHILDREN);
        menu2.addChild(new Menu(1, "查看影片", Command.VIEW_FILMS));
        menu2.addChild(new Menu(2, "增加影片", Command.ADD_FILM));
        menu2.addChild(new Menu(3, "修改影片", Command.UPDATE_FILM));
        menu2.addChild(new Menu(4, "删除影片", Command.DELETE_FILM));
        menu2.addChild(new Menu(5, "返回主菜单", Command.GO_BACK_MAIN));

        Menu menu3 = new Menu(3, "影片计划管理", Command.SHOW_CHILDREN);
        menu3.addChild(new Menu(1, "查看影片计划",
                Command.VIEW_FILM_PLANS));
        menu3.addChild(new Menu(2, "增加影片计划", Command.ADD_FILM_PLAN));
        menu3.addChild(new Menu(3, "修改影片计划",
                Command.UPDATE_FILM_PLAN));
        menu3.addChild(new Menu(4, "删除影片计划",
                Command.DELETE_FILM_PLAN));
        menu3.addChild(new Menu(5, "返回主菜单", Command.GO_BACK_MAIN));

        Menu menu4 = new Menu(4, "订单管理", Command.SHOW_CHILDREN);
        menu4.addChild(new Menu(1, "查看订单", Command.VIEW_ORDERS));
        menu4.addChild(new Menu(2, "返回主菜单", Command.GO_BACK_MAIN));
        Menu menu5 = new Menu(5, "返回登录", Command.GO_BACK_LOGIN);

        SELLER_MENUS=new Menu[]{menu1,menu2,menu3,menu4,menu5};
    }

    /**
     编写启动类，测试菜单展示
     展示时发现展示信息带有内存地址，原因是没有重写 Object 类中的 toString() 方法
     * 展示给定的菜单数组
     * @param menus 菜单数组
     */
    public static void showMenu(Menu[]menus){
        System.out.println("==========================");
        Arrays.stream(menus).forEach(System.out::println);
        System.out.println("==========================");
    }
    /**
     * 展示给定的菜单数组
     * @param menus
     */
    public static void showMenus(Menu[] menus){
        System.out.println("==========================");
        Stream.of(menus).forEach(System.out::println);
        System.out.println("==========================");
    }
}
