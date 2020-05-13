package top.icss.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.icss.entity.ResponsePacket;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author cd
 * @desc
 * @create 2020/4/16 9:45
 * @since 1.0.0
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcClientHandler  extends SimpleChannelInboundHandler<ResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponsePacket response) throws Exception {
        RpcCilentFactory.getInstance().receiveResponse(response);
        log.info("channelRead0: "+ response);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String key = socketAddress.toString();
        RpcCilentFactory.getInstance().removeClient(key);
        log.info("channelInactive:localAddress-->{},-->remoteAddress{} ",ctx.channel().localAddress(),socketAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("Exception：" + cause.getMessage());
    }

}
