package top.icss.chat.codec;

import com.google.gson.*;
import io.netty.channel.ChannelHandlerContext;
import top.icss.chat.command.CommandFactory;
import top.icss.chat.entity.Packet;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * @author cd
 * @desc websocket 编解码
 * @create 2020/4/24 10:56
 * @since 1.0.0
 */
public class WebSocketPacketCodec extends MessageToMessageCodec<TextWebSocketFrame, Packet> {

    final Gson gson = new GsonBuilder().create();
    final JsonParser element = new JsonParser();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet p, List<Object> out) throws Exception {
        String json = gson.toJson(p);
        out.add(new TextWebSocketFrame(json));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame frame, List<Object> in) throws Exception {
        String json = frame.text();
        JsonElement parse = element.parse(json);
        JsonObject object = parse.getAsJsonObject();
        //获取指令
        Byte command = object.get("cm").getAsByte();
        Class<? extends Packet> packet = CommandFactory.getPacket(command);

        //映射实体
        Packet p = gson.fromJson(json, packet);
        in.add(p);
    }
}
