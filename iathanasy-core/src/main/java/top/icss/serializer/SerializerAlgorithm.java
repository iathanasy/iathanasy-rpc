package top.icss.serializer;

/**
 * @author cd
 * @desc 序列化算法
 * @create 2020/4/15 11:18
 * @since 1.0.0
 */
public interface SerializerAlgorithm {

    //Protostuff
    byte DEFAULT = 1;
    //Java
    byte JAVA = 2;
    //json 未实现
    byte JSON = 3;

}
