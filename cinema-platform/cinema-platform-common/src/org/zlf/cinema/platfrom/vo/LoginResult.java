package org.zlf.cinema.platfrom.vo;

import java.io.Serializable;
import org.zlf.cinema.platfrom.entity.User;

/**
 * 登录结果（视图对象 vo）
 * <p>
 * 登录不只是“成功/失败”两个状态，还要把登录者的身份（角色、影院名称等）带回客户端，
 * 客户端才能据此展示对应的主菜单，所以单独封装一个对象，而不是返回一个 int。
 */
public class LoginResult implements Serializable {
    /**
     * 登录成功
     */
    public static final int SUCCESS = 1;
    /**
     * 账号或密码错误
     */
    public static final int FAIL = 0;
    /**
     * 账号不存在
     */
    public static final int NOT_EXIST = -1;
    /**
     * 审核不通过（课件没有定义，这里补一个，避免和“账号不存在”混淆）
     */
    public static final int REJECTED = -2;
    /**
     * 账号正在审批
     */
    public static final int AUDITING = 2;
    /**
     * 账号被冻结
     */
    public static final int FROZEN = 3;

    /**
     * 登录结果标识
     */
    private int code;
    /**
     * 登录成功的用户（密码、安全码已置空）
     */
    private User user;

    public LoginResult(int code) {
        this.code = code;
    }

    public LoginResult(int code, User user) {
        this.code = code;
        this.user = user;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
