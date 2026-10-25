package cinema.platform.server.util;
import org.zlf.cinema.platfrom.entity.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 用户存档文件
     */
    public static final String USER_FILE = "data/user.obj";
    /**
     * 影片存档文件
     */
    public static final String FILM_FILE = "data/film.obj";
    /**
     * 影厅存档文件
     */
    public static final String FILM_HALL_FILE = "data/filmHall.obj";
    /**
     * 影片计划存档文件
     */
    public static final String FILM_PLAN_FILE = "data/filmPlan.obj";
    /**
     * 订单存档文件
     */
    public static final String ORDER_FILE = "data/order.obj";
    /**
     * 解冻申请存档文件
     */
    public static final String UNFROZEN_APPLY_FILE = "data/unfrozenApply.obj";

    /**
     * 保存给定的列表数据至给定的路径文件中
     * @param dataList
     * @param path
     * @param <T>
     * @return
     */
    public static <T> boolean saveData(List<T>dataList,String path){
        File file=new File(path);
        File parent=file.getParentFile();
        if(parent!=null&&!parent.exists()){
            parent.mkdirs();
        }
        try (OutputStream os=new FileOutputStream(file);
        ObjectOutputStream oos=new ObjectOutputStream(os)){
            oos.writeObject(dataList);
            oos.flush();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 读取给定路径文件中存储的数据
     * @param path
     * @param <T>
     * @return
     */
    public static <T> List<T> readData(String path){
        try (InputStream is=new FileInputStream(path);
        ObjectInputStream ois=new ObjectInputStream(is)){
            return (List<T>) ois.readObject();
        }catch (Exception e){
            return new ArrayList<>();
        }
    }



}
