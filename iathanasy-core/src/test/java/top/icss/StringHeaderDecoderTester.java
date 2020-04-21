package top.icss;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import top.icss.utils.RandomUtil;

import java.nio.charset.Charset;

/**
 * @author cd
 * @desc
 * @create 2020/4/13 17:50
 * @since 1.0.0
 */
public class StringHeaderDecoderTester {
    String conent = "Hello Word!";

    @Test
    public void testStringHeaderDecoder(){
        ChannelInitializer<EmbeddedChannel> init = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                //如果不处理拆包 会出现粘包
                ch.pipeline().addLast(new StringHeaderDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(init);
        byte[] bytes = conent.getBytes(Charset.forName("UTF-8"));
        for (int i = 0; i < 100; i++) {
            int rand = RandomUtil.randInMod(10);
            ByteBuf buffer = Unpooled.buffer();
            int len = bytes.length * rand;
            System.out.println(len);
            buffer.writeInt(len); //消息长度
            for (int j = 0; j < rand; j++) {
                buffer.writeBytes(bytes);
            }
            channel.writeInbound(buffer);
        }
    }
}
