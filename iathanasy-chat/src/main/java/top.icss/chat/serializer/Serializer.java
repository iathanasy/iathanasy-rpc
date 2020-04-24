package top.icss.chat.serializer;

/**
 * @author cd
 * @desc
 * @create 2020/4/24 15:15
 * @since 1.0.0
 */
public interface Serializer {

    /**
     * 序列化 序列化(对象 -> 字节数组)
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化 (字节数组 -> 对象)
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
