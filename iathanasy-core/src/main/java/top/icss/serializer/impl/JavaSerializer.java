package top.icss.serializer.impl;

import lombok.extern.slf4j.Slf4j;
import top.icss.serializer.Serializer;
import top.icss.serializer.SerializerAlgorithm;

import java.io.*;

/**
 * @author cd
 * @desc java序列化  需要实现Serializable 接口
 * @create 2020/4/15 11:21
 * @since 1.0.0
 */
@Slf4j
public class JavaSerializer implements Serializer {

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JAVA;
    }

    @Override
    public <T> byte[] serialize(T obj) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Java序列化错误！", e.getMessage());
        }
        return byteOut.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        T result = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
            result = (T)in.readObject();
            in.close();
        } catch (Exception e) {
            log.error("Java反序列化错误！", e.getMessage());
        }
        return result;
    }
}
