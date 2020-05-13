package top.icss.entity;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import top.icss.protocol.command.Command;
import top.icss.protocol.impl.DefaultRpcProtocol;
import top.icss.serializer.SerializerAlgorithm;

/**
 * @author cd
 * @desc 响应
 * @create 2020/4/10 10:26
 * @since 1.0.0
 */
@Slf4j
@Data
@ToString
public class ResponsePacket extends Packet{

    private String id;

    /**
     * 响应结果对象, 也可能是异常对象, 由响应状态决定
     */
    private Object result;

    /**
     * 协议类型
     */
    private byte protocolType = DefaultRpcProtocol.TYPE;;

    /**
     * 序列化
     */
    private byte serializeType = SerializerAlgorithm.DEFAULT;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setError(Throwable cause) {
        result = cause;
    }

    /**
     * 结果状态
     * @return
     */
    public boolean getStatus(){
        if(result instanceof Throwable)
            return false;
        return true;
    }

    @Override
    public byte getCommand() {
        return Command.RESPONSE;
    }
}
