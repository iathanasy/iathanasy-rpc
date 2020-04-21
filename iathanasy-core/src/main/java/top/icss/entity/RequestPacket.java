package top.icss.entity;

import lombok.Data;
import lombok.ToString;
import top.icss.protocol.command.Command;
import top.icss.protocol.impl.DefaultRpcProtocol;
import top.icss.serializer.SerializerAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd
 * @desc
 * @create 2020/4/10 10:06
 * @since 1.0.0
 */
@Data
@ToString
public class RequestPacket extends Packet{

    private String id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 接口类名称
     */
    private String interfaceClassName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] parameters;

    /**
     * 时间戳
     */
    private transient long timestamp;

    /**
     * 附带参数
     */
    private Map<String, String> attachments;

    /**
     * 协议类型
     */
    private byte protocolType = DefaultRpcProtocol.TYPE;

    /**
     * 序列化
     */
    private byte serializeType = SerializerAlgorithm.DEFAULT;

    /**
     * 超时
     */
    private int timeout = 0;

    public void putAttachment(String key, String value) {
        if (attachments == null) {
            attachments = new HashMap<String, String>();
        }
        attachments.put(key, value);
    }

    @Override
    public byte getCommand() {
        return Command.REQUEST;
    }
}
