package top.icss.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.icss.zk.Zk;

/**
 * @author cd
 * @desc http服务
 * @create 2020/5/12 16:55
 * @since 1.0.0
 */
@Slf4j
@Data
public class HttpServer {

    private int port = 9507;
    private int cpuNum = Runtime.getRuntime().availableProcessors();

    final NioEventLoopGroup boss = new NioEventLoopGroup(1);
    final EventLoopGroup work = new NioEventLoopGroup(cpuNum * 2);

    //业务线程池
    final EventLoopGroup biz = new NioEventLoopGroup(cpuNum,new DefaultThreadFactory("biz"));

    private Zk zk;

    public HttpServer(Zk zk,int port) {
        this.zk = zk;
        this.port = port;
    }

    public HttpServer(Zk zk) {
        this.zk = zk;
    }

    /**
     * 启动
     * @throws Exception
     */
    public void start() throws Exception {

        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new HttpServerInitializer(biz,zk));

        b.bind(port).sync().addListeners((future)->{
            if(future.isSuccess()){
                log.info("http server start success ! ");
            }else{
                log.error("http server start fail ! ");
            }
        });
    }

    /**
     * 优雅关闭服务
     */
    public void stop(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
