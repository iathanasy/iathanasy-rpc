package top.icss.chat.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import top.icss.chat.command.Command;
import top.icss.chat.entity.Packet;
import top.icss.chat.entity.PacketMessage;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 14:30
 * @since 1.0.0
 */
@ChannelHandler.Sharable
public class ChatServerHandler extends SimpleChannelInboundHandler<Packet> {

    public final static ChatServerHandler INSTANCE = new ChatServerHandler();

    final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.format("Server:[ %s ] , Client: [ %s ] . \n", ctx.channel().localAddress(), ctx.channel().remoteAddress());
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        //处理是什么指令
        if(packet instanceof PacketMessage) {
            //廣播
            broadcast(ctx, (PacketMessage)packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 广播
     * @param msg
     */
    private void broadcast(ChannelHandlerContext ctx, PacketMessage msg){
        msg.setCommand(Command.RESPONSE_MESSAGE);
        String content = "";
        for (Channel c: channels) {
            msg.setFrom(c.id().toString());
            if (c != ctx.channel()) {
                content = "[" + ctx.channel().remoteAddress() + "] " + msg.getContent();
                msg.setTo(c.id().toString());
                msg.setContent(content);
            } else {
                content = "[you] " + msg.getContent();
                msg.setContent(content);
            }
            c.writeAndFlush(msg );
        }
    }

}
