package top.icss.chat;

import top.icss.chat.initializer.ChatTcpServerInitializer;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 10:11
 * @since 1.0.0
 */
public class ChatTcpServer extends AbstractServer{


    @Override
    public void start() {
        super.start();
        b.bind(5891);
        System.out.println("ChatTcpServer port 5891 start...");
    }

    @Override
    public void doChildHandler() {
        b.childHandler(new ChatTcpServerInitializer());
    }

}
