package top.icss.chat.entity;

import com.google.gson.annotations.SerializedName;
import top.icss.chat.command.Command;
import lombok.Data;
import lombok.ToString;

/**
 * @author cd
 * @desc
 * @create 2020/4/24 11:28
 * @since 1.0.0
 */
@ToString
@Data
public class PacketMessage extends Packet{

    private String from;

    private String to;

    private String content;

    @SerializedName("cm")
    private byte command = Command.REQUEST_MESSAGE;


    @Override
    public byte getCommand() {
        return command;
    }
}
