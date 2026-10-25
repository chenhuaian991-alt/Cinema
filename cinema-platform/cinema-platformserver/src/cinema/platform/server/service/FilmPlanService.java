package cinema.platform.server.service;

import cinema.platform.server.util.FileUtil;
import cinema.platform.server.util.FilmPlanValidator;
import org.zlf.cinema.platfrom.entity.FilmPlan;
import org.zlf.cinema.platfrom.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 影片计划业务：增删改查 + 时间冲突检测
 */
public class FilmPlanService {
    /**
     * 增加影片计划
     *
     * @return 1-成功，0-失败，-1-与已有计划时间冲突
     */
    public static Integer add(FilmPlan plan) {
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        for(FilmPlan p:plans){
            if(FilmPlanValidator.isConflictPlan(plan,p)){
                return -1;
            }
        }
        plan.setId(IdGenerator.generateId(10));
        plans.add(plan);
        return FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE) ? 1 : 0;
    }

    /**
     * 修改影片计划：只改日期和时间，影片、影厅不变
     *
     * @param data id,date,beginTime,endTime,owner
     * @return 1-成功，0-失败，-1-计划不存在，-2-修改后时间冲突
     */
    public static Integer update(Map<String, Object> data) {
        String id = (String) data.get("id");
        String date = (String) data.get("date");
        String beginTime = (String) data.get("beginTime");
        String endTime = (String) data.get("endTime");
        String owner = (String) data.get("owner");
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        FilmPlan target=null;
        int index=-1;
        for (int i = 0; i < plans.size(); i++) {
            FilmPlan p = plans.get(i);
            if (p.getId().equals(id) && p.getOwner().getUsername().equals(owner)) {
                target = p;
                index = i;
                break;
            }
        }
        if(target==null)return -1;
        //先把自己从列表里拿出来，再和自己以外的计划比时间
        plans.remove(index);
        target.setPlayDate(date);
        target.setBeginTime(beginTime);
        target.setEndTime(endTime);
        for (FilmPlan p : plans) {
            if (FilmPlanValidator.isConflictPlan(p, target)) {
                return -2;
            }
        }
        plans.add(target);
        return FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE) ? 1 : 0;
    }

    /**
     * 删除影片计划
     *
     * @param data id-计划编号，owner-商家账号
     * @return 1-成功，0-失败，-1-计划不存在
     */
    public static Integer delete(Map<String, Object> data) {
        String id = (String) data.get("id");
        String owner = (String) data.get("owner");
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        for (int i = 0; i < plans.size(); i++) {
            FilmPlan p = plans.get(i);
            if (p.getId().equals(id) && p.getOwner().getUsername().equals(owner)) {
                plans.remove(i);
                return FileUtil.saveData(plans, FileUtil.FILM_PLAN_FILE) ? 1 : 0;
            }
        }
        return -1;
    }

    /**
     * 查看影片计划
     *
     * @param data filmName-影片名称（y/Y 表示全部，否则模糊匹配），owner-商家账号（不带表示查全部商家的）
     */
    public static List<FilmPlan> view(Map<String, Object> data) {
        String filmName = (String) data.get("filmName");
        String owner = (String) data.get("owner");
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        List<FilmPlan> result = new ArrayList<>();
        for (FilmPlan p : plans) {
            if(!"y".equalsIgnoreCase(filmName)&&!p.getFilm().getName().contains(filmName))continue;
            //owner 为空表示普通用户，可以看所有商家的排片
            if(owner!=null&&!p.getOwner().getUsername().equals(owner))continue;
            result.add(p);
        }
        return result;
    }

    /**
     * 通过编号查影片计划（订座前要拿到影厅和座位图）
     *
     * @return 找到返回计划，找不到返回 null
     */
    public static FilmPlan getById(String id) {
        List<FilmPlan> plans = FileUtil.readData(FileUtil.FILM_PLAN_FILE);
        for (FilmPlan p : plans) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }



}
