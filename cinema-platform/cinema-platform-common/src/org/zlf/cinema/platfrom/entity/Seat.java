package org.zlf.cinema.platfrom.entity;

import java.io.Serializable;

/**
 * 座位
 */
    public class Seat implements Serializable {
    private static final long serialVersionUID = 1L;
/**
 * 排号
 */
    private int row;
  /**
  * * 列号
  * */
    private int col;
 /**
  * * 拥有者
  * */
    private String owner;

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return " ■ ";
    }
}