package top.icss;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 17:34
 * @since 1.0.0
 */
public class StringProcessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String str = (String) msg;
        System.out.println("str: "+ str);
//        ByteBuf buf = (ByteBuf) msg;
//        System.out.println(new String(buf.array(), "UTF-8"));
    }
}
