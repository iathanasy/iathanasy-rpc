package top.icss.register.discover;

/**
 * @author cd
 * @desc 服务发现
 * @create 2020/4/20 9:31
 * @since 1.0.0
 */
public interface ServiceDiscovery {

    /**
     * 发现服务
     * @param serviceName
     * @return
     */
    String discovery(String serviceName);
}
