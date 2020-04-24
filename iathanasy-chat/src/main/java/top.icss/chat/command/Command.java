package top.icss.chat.command;

/**
 * @author cd
 * @desc 命令
 * @create 2020/4/24 11:22
 * @since 1.0.0
 */
public interface Command {

    /**
     * 授权
     */
    byte REQUEST_AUTH = 0;
    byte RESPONSE_AUTH = 1;

    /**
     * 消息
     */
    byte REQUEST_MESSAGE = 2;
    byte RESPONSE_MESSAGE = 3;
}
