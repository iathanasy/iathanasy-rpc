package top.icss.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import top.icss.netty.handler.HttpServerHandler;
import top.icss.zk.Zk;

/**
 * @author cd
 * @desc
 * @create 2020/5/12 16:57
 * @since 1.0.0
 */
public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {

    private EventLoopGroup biz;
    private Zk zk;

    public HttpServerInitializer(EventLoopGroup biz, Zk zk) {
        this.biz = biz;
        this.zk = zk;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpServerCodec());
        //分段聚合
        p.addLast(new HttpObjectAggregator(1024 * 64));
        //分块
        p.addLast(new ChunkedWriteHandler());

        //业务
        p.addLast(biz, new HttpServerHandler(zk));
    }
}
