package top.icss.chat.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import top.icss.chat.codec.WebSocketPacketCodec;
import top.icss.chat.handler.ChatServerHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 15:09
 * @since 1.0.0
 */
public class ChatWebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpServerCodec());
        //分段聚合
        p.addLast(new HttpObjectAggregator(1024*10));
        //分块
        p.addLast(new ChunkedWriteHandler());

        p.addLast(new WebSocketServerProtocolHandler("/websocket"));

        p.addLast(new WebSocketPacketCodec());
        p.addLast(ChatServerHandler.INSTANCE);

    }
}
