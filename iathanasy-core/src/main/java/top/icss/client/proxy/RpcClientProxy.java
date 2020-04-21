package top.icss.client.proxy;

/**
 * @author cd
 * @desc Rpc客户端代理
 * @create 2020/4/16 9:58
 * @since 1.0.0
 */
public interface RpcClientProxy {

    /**
     * 获取代理
     * @param <T>
     * @return
     */
    <T> T getProxyService(Class<T> interfaceClass);
}
