package top.icss.chat.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import top.icss.chat.command.CommandFactory;
import top.icss.chat.entity.Packet;
import top.icss.chat.serializer.Serializer;
import top.icss.chat.serializer.impl.DefaultSerializer;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

/**
 * @author cd
 * @desc Tcp 编解码
 * @create 2020/4/24 10:52
 * @since 1.0.0
 */
public class TcpPacketCodec extends MessageToMessageCodec<ByteBuf, Packet> {

    /**魔数*/
    int magiccode = 0x12345678;

    final Serializer serializer = new DefaultSerializer();


    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        if(packet == null){
            throw new NullPointerException("packet is null");
        }
        ByteBuf buf = ctx.channel().alloc().ioBuffer();

        //魔数
        buf.writeInt(magiccode);
        //版本
        buf.writeByte(packet.getVersion());
        //指令
        buf.writeByte(packet.getCommand());

        //序列化
        byte[] bytes = serializer.serialize(packet);
        int length = bytes.length;

        //数据长度
        buf.writeInt(length);
        //数据
        buf.writeBytes(bytes);

        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //协议：  魔数(4) 版本(1) 指令(1) 数据长度(4) 数据(n)
        if(in.readableBytes() < 4){
            return;
        }
        //验证魔数
        int magic = in.getInt(in.readerIndex());
        if(magiccode != magic){
            ctx.channel().close();
        }
        //跳过魔数
        in.skipBytes(4);
        //获取版本
        byte v = in.readByte();
        //获取指令
        byte command = in.readByte();
        //数据长度
        int length = in.readInt();

        //获取数据
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        //获取实体
        Class<? extends Packet> packet = CommandFactory.getPacket(command);
        // 反序列化
        Packet p = serializer.deserialize(bytes, packet);
        out.add(p);
    }
}
