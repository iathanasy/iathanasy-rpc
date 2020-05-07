package top.icss.protocol;

import top.icss.entity.Packet;
import top.icss.entity.RequestPacket;
import top.icss.entity.ResponsePacket;
import top.icss.protocol.command.Command;
import top.icss.protocol.impl.DefaultRpcProtocol;
import top.icss.serializer.Serializer;
import top.icss.serializer.SerializerAlgorithm;
import top.icss.serializer.impl.DefaultSerializer;
import top.icss.serializer.impl.JavaSerializer;
import top.icss.serializer.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd
 * @desc 指令工厂
 * @create 2020/4/15 11:35
 * @since 1.0.0
 */
public class ProtocolFactory {

    private static Map<Byte, Protocol> protocolMap;
    private static Map<Byte, Serializer> serializerMap;
    private static Map<Byte, Class<? extends Packet>> commandMap;

    static{
        protocolMap = new HashMap<Byte, Protocol>();
        protocolMap.put(DefaultRpcProtocol.TYPE, new DefaultRpcProtocol());

        serializerMap = new HashMap<Byte, Serializer>();
        serializerMap.put(SerializerAlgorithm.JAVA, new JavaSerializer());
        serializerMap.put(SerializerAlgorithm.DEFAULT, new DefaultSerializer());
        serializerMap.put(SerializerAlgorithm.JSON, new JsonSerializer());

        commandMap = new HashMap<Byte, Class<? extends Packet>>();
        commandMap.put(Command.REQUEST, RequestPacket.class);
        commandMap.put(Command.RESPONSE, ResponsePacket.class);
    }

    /**
     * 获取协议
     * @param protocolType
     * @return
     */
    public static Protocol getProtocol(Byte protocolType){
        return protocolMap.get(protocolType);
    }

    /**
     * 获取序列化
     * @param codecType
     * @return
     */
    public static Serializer getSerializer(Byte codecType){
        return serializerMap.get(codecType);
    }

    public static Class<? extends Packet> getPacket(Byte command){
        return commandMap.get(command);
    }

}
