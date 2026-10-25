package org.zlf.cinema.platfrom.message;

import java.io.Serializable;
/**
 * 消息
 * @param <T>
 */

public class Message <T> implements Serializable {
    /**
     * 操作命令
     */
    private int command;
    /**
     * 发送的数据
     */
    private T data;

    public Message(int command, T data) {
        this.command = command;
        this.data = data;
    }

    public int getCommand() {
        return command;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return command+"=>"+data;
    }
}
