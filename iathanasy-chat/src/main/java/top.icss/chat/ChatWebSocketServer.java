package top.icss.chat;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 10:37
 * @since 1.0.0
 */
public class ChatWebSocketServer extends HttpServer {

    @Override
    public void start() {
        super.start();
        System.out.println("ChatWebSocketServer port "+ port +" start...");
    }
}
