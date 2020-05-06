package top.icss.test;

import com.google.gson.*;
import top.icss.chat.command.Command;
import top.icss.chat.entity.Packet;
import top.icss.chat.entity.PacketAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cd
 * @desc
 * @create 2020/4/24 11:40
 * @since 1.0.0
 */
public class GsonTest {

    public static void main(String[] args) {
        Gson gson = new GsonBuilder().create();

        PacketAuth packet = new PacketAuth();
        packet.setId("123");
        packet.setNickName("半仙");
        packet.setCommand(Command.REQUEST_AUTH);

        String json = gson.toJson(packet);
        System.out.printf("Serialised %s%n", json);

        JsonElement parse = new JsonParser().parse(json);
        JsonObject object = parse.getAsJsonObject();
        System.out.println(object.get("cm"));



        Packet packet1 = gson.fromJson(json, PacketAuth.class);
        System.out.println(packet1);

        int count = 16;
        // >> 右移  << 左移
        System.out.println(count >> 1 << 1);
        System.out.println(count >> 5); //减 一半 2一半的一半
        System.out.println(count << 2); //加 上当前

        System.out.println(-16 >>> 1);


        System.out.println((count - 1) >> 1);

        System.out.println((count & 1));

    }
}
