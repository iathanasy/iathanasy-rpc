package top.icss;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author cd
 * @desc
 * @create 2020/4/14 14:53
 * @since 1.0.0
 */
public class ObjectProcessHandler extends SimpleChannelInboundHandler<PacketCodec.MessagePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PacketCodec.MessagePacket messagePacket) throws Exception {
        System.out.println(messagePacket);
        //返回
        ctx.writeAndFlush(messagePacket);
    }
}
