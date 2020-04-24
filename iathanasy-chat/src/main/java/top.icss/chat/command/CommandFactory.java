package top.icss.chat.command;

import top.icss.chat.entity.Packet;
import top.icss.chat.entity.PacketAuth;
import top.icss.chat.entity.PacketMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cd
 * @desc 指令工厂
 * @create 2020/4/24 14:18
 * @since 1.0.0
 */
public class CommandFactory {

    private static Map<Byte,Class<? extends Packet>> commandMap = new HashMap<Byte,Class<? extends Packet>>();

    static{
        commandMap.put(Command.REQUEST_AUTH, PacketAuth.class);
        commandMap.put(Command.RESPONSE_AUTH, PacketAuth.class);

        commandMap.put(Command.REQUEST_MESSAGE, PacketMessage.class);
        commandMap.put(Command.RESPONSE_MESSAGE, PacketMessage.class);
    }


    public static Class<? extends Packet> getPacket(Byte command){
        return commandMap.get(command);
    }
}
