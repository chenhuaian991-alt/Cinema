package cinema.platform.server.service;

import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.entity.Film;
import org.zlf.cinema.platfrom.util.IdGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 影片业务：增删改查
 */
public class FilmService {
    /**
     * 增加影片
     *
     * @return 1-成功，0-失败，-1-同一商家下已存在同名同主演的影片
     */
    public static Integer add(Film film) {
        List<Film>films=FileUtil.readData(FileUtil.FILM_FILE);
        for(Film f:films){
            if(f.getOwner().equals(film.getOwner())&&f.getName().equals(film.getName())&&f.getActor().equals(film.getActor())){
                return -1;
            }
        }
        film.setId(IdGenerator.generateId(10));
        films.add(film);
        return FileUtil.saveData(films,FileUtil.FILM_FILE)?1:0;
    }

    /**
     * 修改影片
     *
     * @return 1-成功，0-失败，-1-影片不存在（或不是自己的影片）
     */
    public static Integer update(Film film) {
        List<Film> films = FileUtil.readData(FileUtil.FILM_FILE);
        for(int i=0;i<films.size();i++){
            Film f=films.get(i);
            if(f.getId().equals(film.getId())&&f.getOwner().equals(film.getOwner())){
                films.set(i,film);
                return FileUtil.saveData(films, FileUtil.FILM_FILE) ? 1 : 0;
            }

        }
        return -1;
    }

    /**
     * 删除影片
     *
     * @param data id-影片编号，owner-商家账号
     * @return 1-成功，0-失败，-1-影片不存在
     */
    public static Integer delete(Map<String, Object> data) {
        String id=(String) data.get("id");
        String owner=(String)data.get("owner");
        List<Film> films = FileUtil.readData(FileUtil.FILM_FILE);
        for (int i = 0; i < films.size(); i++) {
            Film f=films.get(i);
            if (f.getId().equals(id) && f.getOwner().equals(owner)) {
                films.remove(i);
                return FileUtil.saveData(films, FileUtil.FILM_FILE) ? 1 : 0;
            }
        }
        return -1;
    }

    /**
     * 查看影片
     *
     * @param data owner-商家账号，name-影片名称（y/Y 表示全部，否则模糊匹配）
     * @return 影片列表
     */
    public static List<Film> view(Map<String, Object> data) {
        String owner = (String) data.get("owner");
        String name = (String) data.get("name");
        List<Film> films = FileUtil.readData(FileUtil.FILM_FILE);
        List<Film> result = new ArrayList<>();
        for (Film f : films) {
            if(!f.getOwner().equals(owner))continue;
            if("y".equalsIgnoreCase(name)||f.getName().contains(name)){
                result.add(f);
            }
        }
        return result;
    }


}
