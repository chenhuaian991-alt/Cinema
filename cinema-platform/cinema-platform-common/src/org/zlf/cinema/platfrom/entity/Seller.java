package org.zlf.cinema.platfrom.entity;

public class Seller extends User{
    private static final long serialVersionUID = 1L;
    /**
     * 影院名称
     */
    private String cinemaName;
    /**
     * 影院地址
     */
    private String cinemaAddress;

    public Seller(String username, String password, String securityCode,
                  String cinemaAddress,String cinemaName) {
        super(username, password, securityCode);
        this.cinemaName=cinemaName;
        this.cinemaAddress=cinemaAddress;
        this.role=Role.SELLER;
        this.state=0;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaAddress() {
        return cinemaAddress;
    }

    public void setCinemaAddress(String cinemaAddress) {
        this.cinemaAddress = cinemaAddress;
    }

    @Override
    public String toString() {
        String stateStr = state == -1 ? "审核不通过" : state == 0 ? "待审核" : state == 1 ? "正常" : "冻结";
        return username + "\t" + cinemaName + "\t" + cinemaAddress + "\t商家\t" + stateStr;
    }

}