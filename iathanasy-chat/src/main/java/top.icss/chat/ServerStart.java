package top.icss.chat;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 15:23
 * @since 1.0.0
 */
public class ServerStart {
    public static void main(String[] args) {
        Server tcp = new ChatTcpServer();
        Server websocket = new ChatWebSocketServer();
        tcp.start();
        websocket.start();
    }
}
