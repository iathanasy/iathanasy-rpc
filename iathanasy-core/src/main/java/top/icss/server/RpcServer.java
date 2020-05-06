package top.icss.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import top.icss.codec.PacketCodec;
import top.icss.codec.Spliter;
import top.icss.register.RegisterMeta;
import top.icss.register.ServiceRegister;
import top.icss.server.handler.HeartBeatServerHandler;
import top.icss.server.handler.RpcServerHandler;
import top.icss.utils.IocContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd
 * @desc
 * @create 2020/4/9 16:59
 * @since 1.0.0
 */
@Slf4j
public class RpcServer {

    private int threadNum = Runtime.getRuntime().availableProcessors() * 2;
    private int port;
    private Map<String, Object> serviceMap = new HashMap<>();
    private ServiceRegister serviceRegistry;

    final EventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
    final EventLoopGroup work = new NioEventLoopGroup(threadNum, new DefaultThreadFactory("work"));

    final EventLoopGroup biz = new NioEventLoopGroup(10, new DefaultThreadFactory("biz"));

    public RpcServer(ServiceRegister serviceRegistry){
        this(serviceRegistry, 5891);
    }

    public RpcServer(ServiceRegister serviceRegistry, int port){
        this.serviceRegistry = serviceRegistry;
        this.port = port;
    }

    public void init(String basePackage){
        String defaultPackage = "top.icss";
        if(null == basePackage && "".equals(basePackage)){
            basePackage = defaultPackage;
        }
        IocContainer ioc = new IocContainer(basePackage);
        Map<String, Object> beans = ioc.getBeans();
        beans.values().forEach((obj)->{
            serviceMap.put(obj.getClass().getInterfaces()[0].getName(), obj);
        });
    }

    /**
     * 启动
     * @throws Exception
     */
    public void start() throws Exception {
        if(serviceMap.isEmpty()){
            log.warn("rpc service beans empty");
        }
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
//                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            //心跳
                            pipeline.addLast(new IdleStateHandler(10,0,0));
                            pipeline.addLast(new Spliter());
                            //编解码
                            pipeline.addLast(PacketCodec.INSTANCE);
                            //业务处理
                            pipeline.addLast(biz, RpcServerHandler.INSTANCE.setBeans(serviceMap));
                            pipeline.addLast(new HeartBeatServerHandler());
                        }
                    });
            ChannelFuture future = b.bind(port).sync();
            log.info("服务启动成功！");

            // 注册服务
            if (null != serviceRegistry) {
                serviceMap.keySet().forEach((service)->{
                    RegisterMeta meta = new RegisterMeta();
                    meta.setPort(port);
                    meta.setServiceProviderName(service);
                    meta.setVersion("1.0");
                    meta.setWeight(50);
                    serviceRegistry.register(meta);
                });
            }

            future.channel().closeFuture().sync();

        } finally {
            stop();
        }

    }

    /**
     * 关闭
     */
    public void stop(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
