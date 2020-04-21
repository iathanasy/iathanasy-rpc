package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 17:37
 * @since 1.0.0
 */
public class Integer2StringDecoderTester {

    @Test
    public void testerIntegerToString(){
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Byte2IntegerDecoder());
                ch.pipeline().addLast(new Integer2StringDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        for (int i = 0; i < 100; i++) {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(i);
            channel.writeInbound(buffer);
        }
    }
}
