package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 16:56
 * @since 1.0.0
 */
public class Byte2IntegerDecoderTester {

    /**
     * 解码整数
     */
    @Test
    public void testByteToIntegerDecoder(){
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Byte2IntegerDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        };
        //出入站进行单元测试
        EmbeddedChannel channel = new EmbeddedChannel(init);

        for (int i = 0; i < 100; i++) {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(i);
            channel.writeInbound(buffer);
        }
    }
}
