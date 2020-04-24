package top.icss.chat;

import top.icss.chat.initializer.ChatWebSocketServerInitializer;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 10:38
 * @since 1.0.0
 */
public class HttpServer extends AbstractServer {

    protected int port = 5892;

    protected final static ChatWebSocketServerInitializer INSTANCE =   new ChatWebSocketServerInitializer();

    @Override
    public void start() {
        super.start();
        b.bind(port);
        System.out.println("HttpServer port "+ port+" start...");
    }

    @Override
    public void doChildHandler() {
        b.childHandler(INSTANCE);
    }
}
