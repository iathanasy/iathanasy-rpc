package top.icss.chat.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import top.icss.chat.codec.TcpPacketCodec;
import top.icss.chat.handler.ChatServerHandler;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 10:25
 * @since 1.0.0
 */
public class ChatTcpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();

        /*p.addLast(new StringDecoder());
        p.addLast(new StringEncoder());*/

        p.addLast(new TcpPacketCodec());
        p.addLast(ChatServerHandler.INSTANCE);
    }
}
