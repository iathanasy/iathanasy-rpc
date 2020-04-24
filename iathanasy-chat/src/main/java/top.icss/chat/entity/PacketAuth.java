package top.icss.chat.entity;

import com.google.gson.annotations.SerializedName;
import top.icss.chat.command.Command;
import lombok.Data;

/**
 * @author cd
 * @desc
 * @create 2020/4/24 11:28
 * @since 1.0.0
 */
@Data
public class PacketAuth extends Packet{

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String nickName;
    @SerializedName("cm")
    private byte command = Command.REQUEST_AUTH;


    @Override
    public byte getCommand() {
        return command;
    }
}
