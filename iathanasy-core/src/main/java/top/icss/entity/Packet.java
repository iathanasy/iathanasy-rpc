package top.icss.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cd
 * @desc
 * @create 2020/4/15 10:55
 * @since 1.0.0
 */
@Data
public abstract class Packet implements Serializable {

    private byte version = 1;

    /**
     * 指令
     * @return
     */
    public abstract byte getCommand();

    /**
     * 序列化
     * @return
     */
    public abstract byte getSerializeType();

    /**
     * 协议类型
     * @return
     */
    public abstract byte getProtocolType();

}
