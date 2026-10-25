package cinema.platform.server.starter;

import org.zlf.cinema.platfrom.command.Command;
import org.zlf.cinema.platfrom.util.SocketUtil;
import org.zlf.cinema.platfrom.message.Message;
import cinema.platform.server.task.MessageProcessTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 影院平台服务器
 */

public class CinemaPlatformServer {
    private ServerSocket serverSocket;
    public CinemaPlatformServer(int port)throws IOException{
        this.serverSocket=new ServerSocket(port);
    }

    /**
     * 服务器启动
     */
    public void start(){
        while(true){
            try {
                //用户A注册，此时用户B登录
                //等待客户端连接
                Socket socket=serverSocket.accept();
                //启动线程执行消息处理任务
                new Thread(new MessageProcessTask(socket)).start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            CinemaPlatformServer server=new CinemaPlatformServer(8888);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
