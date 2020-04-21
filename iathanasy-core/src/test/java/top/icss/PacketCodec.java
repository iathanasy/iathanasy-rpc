package top.icss;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cd
 * @desc 编解码
 * @create 2020/4/14 14:34
 * @since 1.0.0
 */
public class PacketCodec extends ByteToMessageCodec<PacketCodec.MessagePacket> {

    public final static PacketCodec INSTANCE = new PacketCodec();

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap();

    /**
     * 编码 out
     * @param ctx
     * @param message
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePacket message, ByteBuf out) throws Exception {
        byte[] bytes = serialize(message);
        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(SpliterDecoder.magiccode);
        buf.writeByte(SpliterDecoder.version);
        buf.writeByte(1);
        buf.writeByte(1);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);

        out.writeBytes(buf);
    }

    /**
     * 解码 in
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        byte version = in.readByte();
        byte command = in.readByte();
        byte serialize = in.readByte();
        int length = in.readInt();

        //对象
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        System.out.format("In magic:%s,version:%s,command:%s,serialize:%s,length:%s \n",
                magic,version,command,serialize, length);
        MessagePacket packet = deserialize(bytes, MessagePacket.class);
        out.add(packet);
    }


    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            //实测 objenesis.newInstance(cls); 改成 schema.newMessage() 效率更高些
            /*T message = objenesis.newInstance(cls);*/
            Schema<T> schema = getSchema(clazz);
            T message = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 序列化
     * @param cls
     * @param <T>
     * @return
     */
    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }

    @Data
    @ToString
    public static class MessagePacket{
        String id;
        String content;
    }
}
