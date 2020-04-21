package top.icss.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import top.icss.codec.PacketCodec;
import top.icss.codec.Spliter;
import top.icss.entity.RequestPacket;
import top.icss.entity.ResponsePacket;
import top.icss.serializer.SerializerAlgorithm;

import java.util.concurrent.TimeUnit;

/**
 * @author cd
 * @desc
 * @create 2020/4/10 15:32
 * @since 1.0.0
 */
@Slf4j
public class RpcCilent {

    private static String host = "127.0.0.1";
    private static int port = 5891;

    private static int threadNum = Runtime.getRuntime().availableProcessors() * 2;
    private final static EventLoopGroup work = new NioEventLoopGroup(threadNum, new DefaultThreadFactory("work"));
    private Bootstrap b = new Bootstrap();

    private ChannelFuture future;

    static RpcClientHandler handler = new RpcClientHandler();

    public final static RpcCilent INSTANCE = new RpcCilent();

    public static RpcCilent getInstance(){
        return INSTANCE;
    }

    /**
     * 启动客户端
     * @throws Exception
     */
    public void start() throws Exception{
        b.group(work)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new Spliter());
                        //编解码
                        pipeline.addLast(PacketCodec.INSTANCE);
                        pipeline.addLast(handler);
                    }
                });
    }

    /**
     * 关闭
     */
    public void stop(){
        work.shutdownGracefully();
        RpcCilentFactory.getInstance().removeClient(future.channel().remoteAddress().toString());
        if(future.channel().isOpen()){
            future.channel().close();
        }
        future.channel().close();
    }

    /**
     * 創建客戶端
     * @param host
     * @param port
     * @return
     * @throws Exception
     */
    public RpcCilent createConnect(String host, int port) throws Exception{
        future = b.connect(host, port).sync();
        future.awaitUninterruptibly();
        if(future.isSuccess()){
            log.info("Create connection to " + host + ":" + port + " success!");
            String key="/"+host+":"+port;
            RpcCilentFactory.getInstance().putClient(key, RpcCilent.getInstance());
            return RpcCilent.getInstance();
        }
        if (!future.isDone()) {
            log.error("Create connection to " + host + ":" + port + " timeout!");
            throw new Exception("Create connection to " + host + ":" + port + " timeout!");
        }
        if (future.isCancelled()) {
            log.error("Create connection to " + host + ":" + port + " cancelled by user!");
            throw new Exception("Create connection to " + host + ":" + port + " cancelled by user!");
        }
        if (!future.isSuccess()) {
            log.error("Create connection to " + host + ":" + port + " error", future.cause());
            throw new Exception("Create connection to " + host + ":" + port + " error", future.cause());
        }
        return null;
    }

    /**
     * 发送请求
     * @param request
     * @return
     */
    public void sendRequest(RequestPacket request){
        if(future.channel().isOpen()) {
            ChannelFuture future = this.future.channel().writeAndFlush(request);

            future.addListeners(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> f) throws Exception {
                    if (f.isSuccess()) {
                        return;
                    }
                    String errorMsg = "";
                    // write timeout

                    if (f.isCancelled()) {
                        errorMsg = "Send request to " + future.channel().toString()
                                + " cancelled by user,request id is:"
                                + request.getId();
                    } else if (!future.isSuccess()) {
                        if (future.channel().isOpen()) {
                            // maybe some exception,so close the channel
                            future.channel().close();
                            RpcCilentFactory.getInstance().removeClient(future.channel().remoteAddress().toString());
                        }
                        errorMsg = "Send request to " + future.channel().toString() + " error" + future.cause();
                    }
                    log.error(errorMsg);
                    ResponsePacket response = new ResponsePacket();
                    response.setId(request.getId());
                    response.setVersion(request.getVersion());
                    response.setProtocolType(request.getProtocolType());
                    response.setSerializeType(request.getSerializeType());
                    response.setError(new Throwable(errorMsg));
                    RpcCilentFactory.getInstance().offer(response);
                }
            });
        }
    }

}
