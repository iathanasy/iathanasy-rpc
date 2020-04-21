package top.icss.protocol;

import io.netty.buffer.ByteBuf;
import top.icss.entity.Packet;

/**
 * @author cd
 * @desc 协议类型
 * @create 2020/4/15 10:34
 * @since 1.0.0
 *
 */
public interface Protocol {

    /**魔数*/
    int magiccode = 0x12345678;

    /**
     * 类型
     * @return
     */
    byte getProtocolType();

    /**
     * 解码
     */
    <T> T decode(ByteBuf in, Packet clazz);

    /**
     * 编码
     * @param clazz
     * @return
     */
    void encode(ByteBuf out, Packet clazz);
}
