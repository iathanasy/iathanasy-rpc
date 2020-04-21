package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;
import top.icss.utils.RandomUtil;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author cd
 * @desc 拆包器
 * @create 2020/4/14 10:01
 * @since 1.0.0
 */
public class NettyOpenBoxDecoder {

    String content = "Hello Word！";
    String spliter = "\r\n";
    String spliter1 = "\n";
    String spliter2 = "\t";

    /**魔数*/
    int magiccode = 0x12345678;
    //版本
    byte version = 1;

    /**
     * LineBasedFrameDecoder 使用实例
     * 基于行分隔符的拆包器，TA可以同时处理 \n以及\r\n两种类型的行分隔符
     * maxLength：最大消息长度
     */
    @Test
    public void testLineBasedFrameDecoder(){
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buf = Unpooled.buffer();

            for (int j = 0; j < rand; j++) {
                buf.writeBytes(bytes);
            }
            buf.writeBytes(spliter.getBytes(Charset.forName("UTF-8")));
            channel.writeInbound(buf);
        }
    }


    /**
     * DelimiterBasedFrameDecoder 使用实例
     * 特殊的分隔符作为消息分隔符，回车换行符是他的一种。
     *
     * maxFrameLength：解码的帧的最大长度
     * stripDelimiter：解码时是否去掉分隔符
     * failFast：为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
     * delimiter：分隔符
     */
    @Test
    public void testDelimiterBasedFrameDecoder(){
        final ByteBuf delimiter = Unpooled.copiedBuffer(spliter2.getBytes(Charset.forName("UTF-8")));
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buf = Unpooled.buffer();

            for (int j = 0; j < rand; j++) {
                buf.writeBytes(bytes);
            }
            buf.writeBytes(spliter2.getBytes(Charset.forName("UTF-8")));
            channel.writeInbound(buf);
        }
    }


    /**
     * LengthFieldBasedFrameDecoder 使用实例
     * 基于长度的截断拆包
     * 1.第一个参数是 maxFrameLength 表示的是包的最大长度，超出包的最大长度netty将会做一些特殊处理，后面会讲到
     * 2.第二个参数指的是长度域的偏移量lengthFieldOffset，在这里是0，表示无偏移
     * 3.第三个参数指的是长度域长度lengthFieldLength，这里是4，表示长度域的长度为4
     * 4.第四个参数指的是可调整长度的拆包lengthAdjustment，
     * 5.第五个参数指的是长度的截断拆包initialBytesToStrip, 这里是4，表示获取完一个完整的数据包之后，忽略前面的四个字节，应用解码器拿到的就是不带长度域的数据包
     */
    @Test
    public void testLengthFieldBasedFrameDecoder(){
        final LengthFieldBasedFrameDecoder spliter =
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4);
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(spliter);
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buf = Unpooled.buffer();

            buf.writeInt(bytes.length * rand);
            for (int j = 0; j < rand; j++) {
                buf.writeBytes(bytes);
            }
            channel.writeInbound(buf);
        }
    }


    /**
     * LengthFieldBasedFrameDecoder
     * 基于可调整长度的截断拆包
     */
    @Test
    public void testLengthFieldBasedFrameDecoder1(){
        //4+2
        final LengthFieldBasedFrameDecoder spliter =
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 2, 6);
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(spliter);
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(bytes.length * rand);
            buf.writeChar(version);
            for (int j = 0; j < rand; j++) {
                buf.writeBytes(bytes);
            }
            channel.writeInbound(buf);
        }
    }


    /**
     * LengthFieldBasedFrameDecoder
     * 基于偏移可调整长度的截断拆包
     */
    @Test
    public void testLengthFieldBasedFrameDecoder2(){
        //4+4+2
        final LengthFieldBasedFrameDecoder spliter =
                new LengthFieldBasedFrameDecoder(1024, 2, 4, 4, 10);
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
                channel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                channel.pipeline().addLast(spliter);
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(new StringProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = content.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buf = Unpooled.buffer();
            buf.writeChar(version);
            buf.writeInt(bytes.length * rand);
            buf.writeInt(magiccode);
            for (int j = 0; j < rand; j++) {
                buf.writeBytes(bytes);
            }
            channel.writeInbound(buf);
        }
    }


    /**
     * LengthFieldBasedFrameDecoder
     * 基于偏移可调整长度的截断拆包
     * 对象
     */
    @Test
    public void testLengthFieldBasedFrameDecoder3(){

        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel channel) throws Exception {
//                channel.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                channel.pipeline().addLast(new SpliterDecoder());
                channel.pipeline().addLast(new PacketCodec());
                channel.pipeline().addLast(new ObjectProcessHandler());

            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);

        for (int i = 0; i < 100; i++) {

            PacketCodec.MessagePacket message = new PacketCodec.MessagePacket();
            message.setId(UUID.randomUUID().toString());
            message.setContent(content + i);

            byte[] bytes = PacketCodec.INSTANCE.serialize(message);

            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(SpliterDecoder.magiccode);
            buf.writeByte(SpliterDecoder.version);
            buf.writeByte(1);
            buf.writeByte(1);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            channel.writeInbound(buf);
        }
        channel.flush();

        //取得通道的出站数据帧
        ByteBuf in = (ByteBuf) channel.readOutbound();
        while (null != in) {
            int magic = in.readInt();
            byte version = in.readByte();
            byte command = in.readByte();
            byte serialize = in.readByte();
            int length = in.readInt();

            //对象
            byte[] bytes = new byte[length];
            in.readBytes(bytes, 0, length);

            System.out.format("Out magic:%s,version:%s,command:%s,serialize:%s,length:%s \n",
                    magic,version,command,serialize, length);
            PacketCodec.MessagePacket packet = PacketCodec.INSTANCE.deserialize(bytes, PacketCodec.MessagePacket.class);
            System.out.println(packet);
            in = (ByteBuf) channel.readOutbound();
        }
    }


}
