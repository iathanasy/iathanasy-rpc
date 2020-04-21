package top.icss.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import top.icss.protocol.Protocol;

/**
 * @author cd
 * @desc 基于长度解码
 * @create 2020/4/15 15:49
 * @since 1.0.0
 *
 *   协议
 *   魔数   版本号  协议类型   序列化    指令   数据长度   数据
 *    4       1        1         1         1      4        N字
 */

public class Spliter extends LengthFieldBasedFrameDecoder {

    public Spliter() {
        //长度偏移8
        super(Integer.MAX_VALUE, 8, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if(in.readableBytes() < 4){
            return null;
        }
        //验证魔数
        int magic = Protocol.magiccode;
        if(in.getInt(in.readerIndex()) != magic){
            ctx.channel().close();
            return null;
        }

        return super.decode(ctx, in);
    }
}
