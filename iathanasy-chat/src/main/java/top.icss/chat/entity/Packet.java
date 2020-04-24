package top.icss.chat.entity;


import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author cd
 * @desc
 * @create 2020/4/24 10:55
 * @since 1.0.0
 */
@Data
public abstract class Packet{

    @SerializedName("v")
    byte version = 1;

    /**
     * 命令
     * @return
     */
    public abstract byte getCommand();

}
