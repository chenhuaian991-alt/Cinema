package org.zlf.cinema.platfrom.entity;

import java.io.Serializable;

/**
 * 影片计划
 */
public class FilmPlan implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 编号
     */
    private String id;
    /**
     * 使用影片
     */
    private Film film;
    /**
     * 使用影厅
     */
    private FilmHall hall;
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
     * 拥有者
     */
    private Seller owner;

    public FilmPlan(Film film, FilmHall hall, String playDate,
                    String beginTime, String endTime, Seller owner) {
        this.film = film;
        this.hall = hall;
        this.playDate = playDate;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public FilmHall getHall() {
        return hall;
    }

    public void setHall(FilmHall hall) {
        this.hall = hall;
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

    public Seller getOwner() {
        return owner;
    }

    public void setOwner(Seller owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return id + "\t" + film.getName() + "\t" + hall.getName() + "\t" + playDate
                + "\t" + beginTime + "~" + endTime + "\t" + hall.getRestTickets()
                + "\t" + owner.getCinemaName();
    }
}