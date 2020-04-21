package top.icss.serializer.impl;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import top.icss.serializer.Serializer;
import top.icss.serializer.SerializerAlgorithm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cd
 * @desc Protostuff
 * @create 2020/4/15 16:16
 * @since 1.0.0
 */
@Slf4j
public class DefaultSerializer implements Serializer {

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap();

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.DEFAULT;
    }


    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            log.error("Protostuff序列化错误！", e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            //实测 objenesis.newInstance(cls); 改成 schema.newMessage() 效率更高些
            /*T message = objenesis.newInstance(cls);*/
            Schema<T> schema = getSchema(clazz);
            T message = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            log.error("Protostuff反序列化错误！", e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            cachedSchema.put(cls, schema);
        }
        return schema;
    }
}
