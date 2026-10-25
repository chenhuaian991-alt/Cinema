package cinema.platform.server.service;
import cinema.platform.server.util.FileUtil;
import org.zlf.cinema.platfrom.entity.Seller;
import org.zlf.cinema.platfrom.entity.UnfrozenApply;
import org.zlf.cinema.platfrom.entity.User;
import org.zlf.cinema.platfrom.util.IdGenerator;
import org.zlf.cinema.platfrom.vo.LoginResult;

import java.util.List;
public class UserService {
    /**
     * 注册 / 入驻申请
     * <p>
     * 普通用户和商家都往 user.obj 里存，用 Seller 是否为 User 的子类来区分身份，
     * 所以注册和入驻申请可以共用这一段逻辑。
     *
     * @param data User（普通用户）或 Seller（商家）
     * @return 1-成功，0-失败，-1-账号或影院名称已被使用
     */
    public static Integer register(Object data){
        User user=(User) data;
        List<User>users=FileUtil.readData(FileUtil.USER_FILE);
        for(User u:users){
            //账号唯一
            if(u.getUsername().equals(user.getUsername()))return -1;
            //商家的影院名称也唯一
            if(u instanceof Seller&& user instanceof Seller){
                Seller s1=(Seller) u;
                Seller s2=(Seller) user;
                if(s1.getCinemaName().equals(s2.getCinemaName()))return -1;
            }
        }
        users.add(user);
        return FileUtil.saveData(users,FileUtil.USER_FILE)?1:0;
    }

    /**
     * 登录
     *
     * @param data 只填了账号、密码的 User
     * @return LoginResult：标识 + 登录者身份
     */
    public static LoginResult login(User data) {
        List<User>users=FileUtil.readData(FileUtil.USER_FILE);
        //系统第一次运行，内置一个管理员账号，否则没有人能审核商家
        boolean hasManager=false;
        for(User u:users){
            if(u.getRole()== User.Role.MANAGER){
                hasManager=true;
                break;
            }
        }
        if(!hasManager){
            User manager=new User("admin", "123456", "ZLF");
            manager.setRole(User.Role.MANAGER);
            manager.setState(1);
            users.add(manager);
            FileUtil.saveData(users,FileUtil.USER_FILE);
        }

        for(User u:users){
            if(!u.getUsername().equals(data.getUsername())){
                continue;
            }
            //账号存在，再判断状态
            if(u.getState()==2){
                return new LoginResult(LoginResult.FROZEN);
            }
            if(u.getState()==0){
                return new LoginResult(LoginResult.AUDITING);
            }
            if(u.getState()==-1){
                return new LoginResult(LoginResult.REJECTED);
            }
            if(!u.getPassword().equals(data.getPassword())){
                return new LoginResult(LoginResult.FAIL);
            }
            //登录成功：把身份带回去，但密码、安全码不能回传
            User loginUser=copyOf(u);
            loginUser.setPassword(null);
            loginUser.setSecurityCode(null);
            return new LoginResult(LoginResult.SUCCESS,loginUser);
        }
        return new LoginResult(LoginResult.NOT_EXIST);
    }

    /**
     * 找回密码
     *
     * @param data 账号 + 安全码
     * @return 安全码正确返回密码，否则返回 null
     */
    public static String getPasswordBack(User data){
        List<User>users=FileUtil.readData(FileUtil.USER_FILE);
        for(User u:users){
            if(!u.getUsername().equals(data.getUsername()))continue;
            if(u.getSecurityCode()!=null&&u.getSecurityCode().equals(data.getSecurityCode())){
                return u.getPassword();
            }
            return null;
        }
        return null;
    }

    /**
     * 申请解冻
     *
     * @param apply 账号 + 解冻理由
     * @return 1-成功，0-失败，-1-账号不存在，-2-账号状态不允许申请，-3-已有待处理的申请
     */
    public static Integer unfrozenApply(UnfrozenApply apply) {
        List<User>users=FileUtil.readData(FileUtil.USER_FILE);
        User target=null;
        for(User u:users){
            if(u.getUsername().equals(apply.getUsername())){
                target=u;
                break;
            }
        }
        if(target==null){
            return -1;
        }
        //只有被冻结的账号才需要解冻
        if(target.getState()!=2){
            return -2;
        }
        List<UnfrozenApply>applies=FileUtil.readData(FileUtil.UNFROZEN_APPLY_FILE);
        for(UnfrozenApply ua:applies){
            if(ua.getUsername().equals(target.getUsername())&&ua.getState()==0){
                return -3;
            }
        }
        apply.setId(IdGenerator.generateId(10));
        apply.setState(0);
        applies.add(apply);
        return FileUtil.saveData(applies,FileUtil.UNFROZEN_APPLY_FILE)?1:0;
    }


    /**
     * 复制一个用户对象：需要把“去掉密码的副本”发给客户端，
     * 不能直接把存档里的对象置空，否则会把密码写没了。
     */
    private static User copyOf(User u){
        User copy;
        if(u instanceof Seller){
            Seller s=(Seller) u;
            copy=new Seller(s.getUsername(),s.getPassword(), s.getSecurityCode(), s.getCinemaAddress(),s.getCinemaName());

        }else {
            copy=new User(u.getUsername(),u.getPassword(), u.getSecurityCode());
        }
        copy.setRole(u.getRole());
        copy.setState(u.getState());
        return copy;
    }


}
