package top.icss.zk;

/**
 * @author cd
 * @desc
 * @create 2020/5/12 17:21
 * @since 1.0.0
 */
public class ZkConstant {
    /**
     * zk服务地址
     */
    public static final String ZK_ADDR = "127.0.0.1:2181";

    /**
     * 连接超时
     */
    public static final int ZK_SESSION_TIMEOUT = 5000;

    /**
     * 服务注册路径
     */
    public static final String ZK_REGISTRY_PATH = "/iathanasy/provider";
}
