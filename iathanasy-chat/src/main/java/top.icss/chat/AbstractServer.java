package top.icss.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 10:16
 * @since 1.0.0
 */
public abstract class AbstractServer implements Server{

    private AtomicBoolean flag = new AtomicBoolean(false);

    protected EventLoopGroup boss;
    protected EventLoopGroup work;

    protected ServerBootstrap b;

    @Override
    public void start() {
        if(flag.compareAndSet(false, true)) {
            doStart();
        }
    }

    @Override
    public void stop() {
        if(flag.get()) {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    /**
     * 实现 childHandler
     */
    public abstract void doChildHandler();

    private void doStart(){
        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();

        b = new ServerBootstrap();
        b.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO));

        doChildHandler();
    }

}
