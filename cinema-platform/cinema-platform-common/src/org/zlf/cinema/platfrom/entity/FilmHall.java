package org.zlf.cinema.platfrom.entity;
/**
 * 影厅
 */
import java.io.PrintStream;
import java.io.Serializable;
public class FilmHall implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * 编号
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 总排数
     */
    private int totalRow;
    /**
     * 总列数
     */
    private int totalCol;
    /**
     * 座位列表
     */
    private Seat[][] seats;
    /**
     * 拥有者
     */
    private String owner;

    public FilmHall(String name, int totalRow, int totalCol, String owner) {
        this.name = name;
        this.totalRow = totalRow;
        this.totalCol = totalCol;
        this.owner = owner;
        //总排数和总列数决定座位列表的大小
        this.seats=new Seat[totalRow][totalCol];
        for(int i=0;i<totalRow;i++){
            for(int j=0;j<totalCol;j++){
                this.seats[i][j]=new Seat(i,j);
            }
        }
    }

    /**
     * 检测给定排号和列号的座位是否已经被订购
     */
    public boolean hasOwner(int row,int col){
        return seats[row][col].getOwner()!=null;
    }

    /**
     * 换座：原座位置空，新座位设置拥有者
     */
    public void changeSeat(int originalRow, int originalCol, int row, int col) {
        String owner=seats[originalRow][originalCol].getOwner();
        seats[originalRow][originalCol].setOwner(null);
        seats[row][col].setOwner(owner);
    }

    /**
     * 获取余票：拥有者为空的座位即为余票
     */
    public int getRestTickets() {
        int tickets=0;
        for(Seat[] seatArr:seats){
            for(Seat seat:seatArr){
                if(seat.getOwner()==null)tickets++;
            }
        }
        return tickets;
    }

    /**
     * 订购座位
     */
    public void orderSeat(int row, int col, String owner){
        seats[row][col].setOwner(owner);
    }

    /**
     * 取消订座
     */
    public void cancelSeat(int row, int col) {
        seats[row][col].setOwner(null);
    }

    /**
     * 显示座位信息：未被订购的座位使用标准输出，已被订购的座位使用错误输出（控制台显示为红色）
     */
    public void showSeatInfo() {
        System.out.print(" ");
        for(int j=0;j<totalCol;j++){
            System.out.print(" "+j+" ");
        }
        System.out.println();
        for(int i=0;i<totalRow;i++){
            System.out.print(i);
            for(int j=0;j<totalCol;j++){
                PrintStream ps=seats[i][j].getOwner()==null?System.out:System.err;
                ps.println(seats[i][j]);
                try{
                    Thread.sleep(20L);
                } catch (InterruptedException e) {

                }
            }
            System.out.println();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalCol() {
        return totalCol;
    }

    public void setTotalCol(int totalCol) {
        this.totalCol = totalCol;
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public void setSeats(Seat[][] seats) {
        this.seats = seats;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return id + "\t" + name + "\t" + totalRow + "\t" + totalCol;
    }
}