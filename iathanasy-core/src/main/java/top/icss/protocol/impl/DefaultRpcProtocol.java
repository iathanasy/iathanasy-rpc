package top.icss.protocol.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import top.icss.entity.Packet;
import top.icss.entity.ResponsePacket;
import top.icss.protocol.Protocol;
import top.icss.protocol.ProtocolFactory;
import top.icss.serializer.Serializer;

/**
 * @author cd
 * @desc 默认协议解析
 * @create 2020/4/15 11:05
 * @since 1.0.0
 *  byte     1字节
 *  short    2字节
 *  char     2字节（C语言中是1字节）可以存储一个汉字
 *  int      4字节
 *  long     8字节
 *  float    4字节
 *  double   8字节
 *  boolean  false/true(理论上占用1bit,1/8字节，实际处理按1byte处理)
 *
 *  协议
 *  魔数   版本号  协议类型   序列化    指令   数据长度   数据
 *   4       1        1         1         1      4        N字节
 */
public class DefaultRpcProtocol implements Protocol {

    public static byte TYPE = 1;

    @Override
    public byte getProtocolType() {
        return TYPE;
    }

    @Override
    public <T> T decode(ByteBuf in,Packet clazz) {
        /*int magic = in.readInt();
        byte version = in.readByte();
        //协议类型
        byte protocolType = in.readByte();*/
        //跳过魔数 版本 协议
        in.skipBytes(4);
        in.skipBytes(1);
        in.skipBytes(1);

        //序列化
        byte serialize = in.readByte();
        //指令
        byte command = in.readByte();
        // 数据包长度
        int length = in.readInt();

        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        /*获取序列化*/
        Serializer serializer = ProtocolFactory.getSerializer(serialize);
        /*获取指令类*/
        Class<? extends Packet> packet = ProtocolFactory.getPacket(command);
        if(serializer != null && packet != null) {
            return (T)serializer.deserialize(bytes, packet);
        }
        return null;
    }

    @Override
    public void encode(ByteBuf out, Packet packet) {
        //序列化
        Serializer serializer = ProtocolFactory.getSerializer(packet.getSerializeType());
        byte[] bytes = serializer.serialize(packet);

        //具体编码
        out.writeInt(magiccode);
        out.writeByte(packet.getVersion());
        out.writeByte(packet.getProtocolType());
        out.writeByte(packet.getSerializeType());
        out.writeByte(packet.getCommand());
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }
}
