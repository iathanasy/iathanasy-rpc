package top.icss.utils;

/**
 * @author cd
 * @desc
 * @create 2020/5/6 16:33
 * @since 1.0.0
 */
public class StringUtil {

    /**
     * 将字符串首字母转换为小写
     * @param name
     * @return
     */
    public static String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }
}
