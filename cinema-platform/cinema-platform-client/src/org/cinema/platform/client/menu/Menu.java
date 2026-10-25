package org.cinema.platform.client.menu;

import java.util.ArrayList;
import java.util.List;
/**
 * 菜单
 */
public class Menu {
    /**
     * 序号
     */
    private int order;
    /**
     * 名称
     */
    private String name;
    /**
     * 菜单命令
     */
    private int command;
    /**
     * 子菜单列表
     */
    private List<Menu>children=new ArrayList<>();

    public Menu(int order, String name, int command) {
        this.order = order;
        this.name = name;
        this.command = command;
    }

    /**
     * 添加子菜单
     * @param child
     */
    public void addChild(Menu child){
        children.add(child);
    }

    @Override
    public String toString() {
        return order + "." + name;
    }

    public Menu[] getChildren(){
        return children.toArray(new Menu[0]);
    }

    public int getOrder() {
        return order;
    }

    public String getName() {
        return name;
    }

    public int getCommand() {
        return command;
    }
}
