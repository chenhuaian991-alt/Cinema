package cinema.platform.server.service;

import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.UnfrozenApply;
import org.zlf.cinema.platfrom.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class AdminService {
    /**
     * 管理员业务：商家管理、用户管理、解冻申请管理
     */


    /**
     * 查看商家（按影院名称模糊匹配）
     *
     * @param cinemaName 影院名称，输入 y/Y 表示查看全部
     * @return 商家列表
     */
    public static List<Seller> viewSellers(String cinemaName) {
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        List<Seller>result=new ArrayList<>();
        for(User u:users){
            if(!(u instanceof Seller)){
                continue;
            }
            Seller seller=(Seller) u;
            if("y".equalsIgnoreCase(cinemaName)||seller.getCinemaName().contains(cinemaName)){
                result.add(seller);
            }
        }
        return result;
    }

    /**
     * 查看入驻申请：只看待审核（state == 0）的商家
     *
     * @param cinemaName 影院名称，输入 y/Y 表示查看全部
     */
    public static List<Seller> viewEntryApply(String cinemaName){
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        List<Seller> result = new ArrayList<>();
        for (User u : users) {
            if(!(u instanceof Seller))continue;
            Seller seller=(Seller) u;
            if(seller.getState()!=0){
                continue;
            }
            if("y".equalsIgnoreCase(cinemaName)||seller.getCinemaName().contains(cinemaName)){
                result.add(seller);
            }
        }
        return result;
    }

    /**
     * 审核商家
     *
     * @param data username-商家账号，state-审核结果（1-通过，-1-不通过）
     * @return 1-成功，0-失败，-1-商家不存在，-2-商家不处于待审核状态
     */
    public static Integer auditSeller(Map<String, Object> data) {
        String username=(String)data.get("username");
        Integer state=(Integer) data.get("state");
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        for (User u : users) {
            if(!(u instanceof Seller)||!u.getUsername().equals(username)){
                continue;
            }
            //只有待审核的商家才能审核，避免把已经正常的商家改回去
            if(u.getState()!=0){
                return -2;
            }
            u.setState(state);
            return FileUtil.saveData(users,FileUtil.USER_FILE)?1:0;
        }
        return -1;
    }

    /**
     * 冻结商家
     *
     * @param username 商家账号
     * @return 1-成功，0-失败，-1-商家不存在，-2-商家不处于正常状态
     */
    public static Integer frozenSeller(String username) {
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        for (User u : users) {
            if(!(u instanceof Seller)||!u.getUsername().equals(username))continue;
            if(u.getState()!=1)return -2;
            u.setState(2);
            return FileUtil.saveData(users,FileUtil.USER_FILE)?1:0;
        }
        return -1;
    }

    /**
     * 查看用户：普通用户列表，商家不在这里展示
     */
    public static List<User> viewUsers() {
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        List<User> result = new ArrayList<>();
        for(User u:users){
            if(!(u instanceof Seller)){
                result.add(u);
            }
        }
        return result;
    }

    /**
     * 冻结用户
     *
     * @param username 用户账号
     * @return 1-成功，0-失败，-1-用户不存在，-2-用户不处于正常状态
     */
    public static Integer frozenUser(String username){
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        for (User u : users) {
            if(!u.getUsername().equals(username))continue;
            if(u.getState()!=1)return -2;
            u.setState(2);
            return FileUtil.saveData(users, FileUtil.USER_FILE) ? 1 : 0;
        }
        return -1;
    }

    /**
     * 查看全部解冻申请
     */
    public static List<UnfrozenApply> viewUnfrozenApplies() {
        return FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
    }

    /**
     * 审批解冻申请
     *
     * @param data id-申请编号，state-审批结果（1-通过，2-驳回）
     * @return 1-成功，0-失败，-1-申请不存在，-2-申请不是待处理状态
     */
    public static Integer auditUnfrozenApply(Map<String, Object> data) {
        String id=(String) data.get("id");
        Integer state=(Integer) data.get("state");
        //审批结果只能是 1（通过）或 2（驳回），其它值会把状态机写乱
        if(id==null||state==null||(state!=1&&state!=2)){
            return 0;
        }
        List<UnfrozenApply> applies = FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
        UnfrozenApply apply = null;
        for (UnfrozenApply a : applies) {
            if(a.getId().equals(id)){
                apply=a;
                break;
            }
        }
        if(apply==null)return -1;
        if(apply.getState()!=0)return -2;

        //驳回：只动一个文件，写失败就没有任何副作用
        if(state==2){
            apply.setState(2);
            if(FileUtil.saveData(applies,FileUtil.UNFROZEN_APPLY_FILE))return 1;
            apply.setState(0);//写失败，内存里的状态也退回“待处理”
            return 0;
        }

        //通过：要改两个文件（账号解冻 + 申请已通过），必须“要么都成，要么都不成”
        List<User> users = FileUtil.readData(FileUtil.USER_FILE);
        User target = null;
        for (User u : users) {
            if(u.getUsername().equals(apply.getUsername())){
                target=u;
                break;
            }
        }
        if(target==null){//账号不存在（被删了），申请成了孤儿，不能解冻
            return -1;
        }
        int oldUserState = target.getState();

        //第一步：账号解冻并落盘
        target.setState(1);
        if(!FileUtil.saveData(users,FileUtil.USER_FILE)){
            target.setState(oldUserState);//内存回滚
            return 0;
        }
        //第二步：申请标记已通过
        apply.setState(1);
        if(!FileUtil.saveData(applies,FileUtil.UNFROZEN_APPLY_FILE)){
            //补偿：第二步失败就把账号改回冻结并重新落盘，
            //避免出现“账号已解冻、申请还显示待处理”的不一致
            target.setState(oldUserState);
            FileUtil.saveData(users,FileUtil.USER_FILE);
            apply.setState(0);//内存回滚
            return 0;
        }
        return 1;
    }
}
