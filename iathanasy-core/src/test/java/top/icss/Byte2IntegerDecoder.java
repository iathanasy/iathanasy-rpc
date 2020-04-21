package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 17:02
 * @since 1.0.0
 */
public class Byte2IntegerDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }
        int i = in.readInt();
        System.out.println("解码："+ i);
        out.add(i);
    }
}
