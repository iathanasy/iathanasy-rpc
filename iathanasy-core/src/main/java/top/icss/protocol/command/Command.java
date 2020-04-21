package top.icss.protocol.command;

/**
 * @author cd
 * @desc 指令
 * @create 2020/4/15 10:49
 * @since 1.0.0
 */
public interface Command {

    /**
     * 请求
     */
    byte REQUEST = (byte) 0;

    /**
     * 响应
     */
    byte RESPONSE = (byte) 1;
}
