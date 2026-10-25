package cinema.platform.server.util;

import org.zlf.cinema.platfrom.entity.FilmPlan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
/**
 * 影片计划校验器：主要检测同一影厅的时间是否冲突
 * <p>
 * 冲突判定：同一商家、同一天、同一影厅，两条计划的时间段有交集。
 * 交集条件：begin1 < end2 && begin2 < end1
 */
public class FilmPlanValidator {
   private static final SimpleDateFormat FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 检测两个影片计划是否冲突
     *
     * @param p1 已存在的计划
     * @param p2 待新增/待修改的计划
     * @return true 表示冲突，不能保存
     */
    public static boolean isConflictPlan(FilmPlan p1,FilmPlan p2){
        //不同商家的影厅互不影响
        if(!p1.getOwner().getUsername().equals(p2.getOwner().getUsername())){
            return false;
        }
        //不同影厅互不影响，同一影院两场不同厅的电影可以同时放映
        if(!p1.getHall().getId().equals(p2.getHall().getId())){
            return false;
        }
        //不同日期互不影响
        if(!p1.getPlayDate().equals(p2.getPlayDate())){
            return false;
        }
        String date=p1.getPlayDate();
        try {
            long begin1=FORMAT.parse(date+" "+p1.getBeginTime()).getTime();
            long begin2=FORMAT.parse(date+" "+p2.getBeginTime()).getTime();
            long end1=FORMAT.parse(date+" "+p1.getEndTime()).getTime();
            long end2=FORMAT.parse(date+" "+p2.getEndTime()).getTime();
            //首尾相接（前一场 12:00 结束、后一场 12:00 开始）不算冲突
            return begin1<end2&&begin2<end1;
        }catch (ParseException e){
            return false;
        }
    }
}
