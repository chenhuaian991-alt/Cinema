package org.zlf.cinema.platfrom.entity;

import java.io.Serializable;

/**
 * 订单
 */

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 编号
     */
    private String id;
    /**
     * 影片计划编号
     */
    private String filmPlanId;
    /**
     * 影片名称
     */
    private String filmName;
    /**
     * 影厅名称
     */
    private String filmHallName;
    /**
     * 播放日期
     */
    private String playDate;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 排号
     */
    private int row;
    /**
     * 列号
     */
    private int col;
    /**
     * 订单所属用户
     */
    private String user;
    /**
     * 订单所属商家
     */
    private Seller seller;
    /**
     * 订单状态：0-已退订 1-正常
     */
    private int state = 1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilmPlanId() {
        return filmPlanId;
    }

    public void setFilmPlanId(String filmPlanId) {
        this.filmPlanId = filmPlanId;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getFilmHallName() {
        return filmHallName;
    }

    public void setFilmHallName(String filmHallName) {
        this.filmHallName = filmHallName;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        String seatInfo = "第" + row + "排第" + col + "列";
        return id + "\t" + seller.getCinemaName() + "\t" + filmName + "\t" + filmHallName
                + "\t" + seatInfo + "\t" + playDate + " " + beginTime + "~" + endTime
                + "\t" + seller.getCinemaAddress() + "\t" + (state == 1 ? "正常" : "已退订");
    }
}