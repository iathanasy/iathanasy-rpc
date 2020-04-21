package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author cd
 * @desc 基于长度
 * @create 2020/4/14 14:09
 * @since 1.0.0
 *
 * byte     1字节
 * short    2字节
 * char     2字节（C语言中是1字节）可以存储一个汉字
 * int      4字节
 * long     8字节
 * float    4字节
 * double   8字节
 * boolean  false/true(理论上占用1bit,1/8字节，实际处理按1byte处理)
 *
 * 协议
 * 魔数   版本号   序列化    指令   数据长度   数据
 *  4       1        1         1       4        N字节
 */
public class SpliterDecoder extends LengthFieldBasedFrameDecoder {
    /**魔数*/
    public static int magiccode = 0x12345678;
    //版本
    public static byte version = 1;

    public SpliterDecoder() {
        //长度字节偏移7
        super(Integer.MAX_VALUE, 7, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if(in.readableBytes() < 4){
            return null;
        }

        int magic = in.getInt(in.readerIndex());
        if(magiccode != magic){
            ctx.channel().close();
            return null;
        }

        return super.decode(ctx, in);
    }
}
