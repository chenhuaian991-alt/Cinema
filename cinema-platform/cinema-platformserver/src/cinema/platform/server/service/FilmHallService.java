package cinema.platform.server.service;

import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.entity.FilmHall;
import org.zlf.cinema.platfrom.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 影厅业务：增删改查
 */
public class FilmHallService {
    /**
     * 增加影厅
     *
     * @return 1-成功，0-失败，-1-同一商家下已存在同名影厅
     */
    public static Integer add(FilmHall hall) {
        List<FilmHall> halls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        for (FilmHall h : halls) {
            //同名影厅只在同一个商家内算重复
            if (h.getOwner().equals(hall.getOwner()) && h.getName().equals(hall.getName())) {
                return -1;
            }
        }
        hall.setId(IdGenerator.generateId(10));
        halls.add(hall);
        return FileUtil.saveData(halls, FileUtil.FILM_HALL_FILE) ? 1 : 0;
    }

    /**
     * 修改影厅：总排数、总列数改变后会重新生成座位，原有订座记录会被清空
     *
     * @return 1-成功，0-失败，-1-影厅不存在
     */
    public static Integer update(FilmHall hall) {
        List<FilmHall> halls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        for (int i = 0; i < halls.size(); i++) {
            FilmHall h = halls.get(i);
            if (h.getId().equals(hall.getId()) && h.getOwner().equals(hall.getOwner())) {
                halls.set(i, hall);
                return FileUtil.saveData(halls, FileUtil.FILM_HALL_FILE) ? 1 : 0;
            }
        }
        return -1;
    }

    /**
     * 删除影厅
     *
     * @param data id-影厅编号，owner-商家账号
     * @return 1-成功，0-失败，-1-影厅不存在
     */
    public static Integer delete(Map<String, Object> data) {
        String id = (String) data.get("id");
        String owner = (String) data.get("owner");
        List<FilmHall> halls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        for (int i = 0; i < halls.size(); i++) {
            FilmHall h=halls.get(i);
            if (h.getId().equals(id) && h.getOwner().equals(owner)) {
                halls.remove(i);
                return FileUtil.saveData(halls, FileUtil.FILM_HALL_FILE) ? 1 : 0;
            }
        }
        return -1;
    }

    /**
     * 查看影厅：只返回该商家自己的影厅
     */
    public static List<FilmHall> view(String owner) {
        List<FilmHall> halls = FileUtil.readData(FileUtil.FILM_HALL_FILE);
        List<FilmHall> result = new ArrayList<>();
        for (FilmHall h : halls) {
            if (h.getOwner().equals(owner)) {
                result.add(h);
            }
        }
        return result;
    }
}
