package top.icss.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.icss.chat.codec.TcpPacketCodec;
import top.icss.chat.entity.PacketMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author cd
 * @desc
 * @create 2020/4/23 16:06
 * @since 1.0.0
 */
public class ChatTcpClient {

    public static void main(String[] args) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline p = channel.pipeline();
                            p.addLast(new TcpPacketCodec());
                            p.addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if(msg instanceof PacketMessage){
                                        System.err.println(((PacketMessage) msg).getFrom() + ": "+ ((PacketMessage) msg).getContent());
                                    }

                                    /*ctx.executor().schedule(()->{
                                        String str = msg + "" *//*+ count.getAndIncrement()*//*;
                                        ctx.writeAndFlush(str);
                                    }, 10, TimeUnit.SECONDS);*/
                                    super.channelRead(ctx, msg);
                                }
                            });
                        }
                    });
            Channel channel = b.connect("127.0.0.1", 5891).sync().channel();
            ChannelFuture future = null;
            PacketMessage message = new PacketMessage();
            message.setFrom(channel.id().toString());
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if(null == line) break;

                message.setContent(line);
                future = channel.writeAndFlush(message);

                if ("bye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }

                if(null != future){
                    future.sync();
                }
            }
        } finally {
            group.shutdownGracefully();
        }

    }
}
