package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 17:47
 * @since 1.0.0
 */
public class StringHeaderDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }

        //设置读取标记
        in.markReaderIndex();

        //获取长度
        int len = in.readInt();

        if(in.readableBytes() < len){
            //回滚标记位
            in.resetReaderIndex();
            return;
        }

        //读取数据
        byte[] bytes = new byte[len];
        in.readBytes(bytes, 0, len);
        out.add(new String(bytes, "UTF-8"));
    }
}
