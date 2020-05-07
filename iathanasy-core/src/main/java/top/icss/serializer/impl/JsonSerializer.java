package top.icss.serializer.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import top.icss.serializer.Serializer;
import top.icss.serializer.SerializerAlgorithm;

/**
 * @author cd
 * @desc Json序列化
 * @create 2020/5/7 15:52
 * @since 1.0.0
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public <T> byte[] serialize(T obj) {

        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {

        return JSON.parseObject(data, clazz);
    }
}
