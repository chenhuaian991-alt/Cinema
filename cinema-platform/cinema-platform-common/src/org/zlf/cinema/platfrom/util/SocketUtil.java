package org.zlf.cinema.platfrom.util;

import java.io.*;
import java.net.Socket;
/**
 * 套接字工具类
 */
public class SocketUtil {
    /**
     * 发送消息
     * @param socket
     * @param msg
     * @param <T>
     */
    public static <T> void sendMsg(Socket socket,T msg){
        try {
            //获取输出流
            OutputStream os= socket.getOutputStream();
            //将输出流包装为对象输出流
            ObjectOutputStream oos=new ObjectOutputStream(os);
            //对象输出流写对象，也就是发送消息
            oos.writeObject(msg);
            oos.flush();
            socket.shutdownOutput();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 接收消息
     * @param socket
     * @param <T>
     * @return
     */
    public static <T> T receiveMsg(Socket socket){
        try {
            InputStream is=socket.getInputStream( );
            ObjectInputStream ois=new ObjectInputStream(is);
            T t=(T)ois.readObject();
            socket.shutdownInput();
            return t;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
