package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import top.icss.entity.Packet;
import top.icss.protocol.Protocol;
import top.icss.protocol.ProtocolFactory;

import java.util.List;

/**
 * @author cd
 * @desc 编解码器
 * @create 2020/4/15 15:33
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class PacketCodec extends MessageToMessageCodec<ByteBuf,Packet> {
    public final static PacketCodec INSTANCE = new PacketCodec();

    /**
     * 编码器
     * @param ctx
     * @param packet
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, List<Object> out) throws Exception {
        if(packet == null){
            throw new NullPointerException("packet is null");
        }
        ByteBuf buf = ctx.channel().alloc().ioBuffer();
        Protocol protocol = ProtocolFactory.getProtocol(packet.getProtocolType());
        protocol.encode(buf, packet);
        out.add(buf);
    }

    /**
     * 解码器
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //标记
        in.markReaderIndex();
        //跳过魔数 版本
        in.skipBytes(4);
        in.skipBytes(1);

        //协议类型
        byte protocolType = in.readByte();
        Protocol protocol = ProtocolFactory.getProtocol(protocolType);
        //回到标记位置
        in.resetReaderIndex();
        Object result = protocol.decode(in, null);

        if (result != null) {
            out.add(result);
        }
    }
}
