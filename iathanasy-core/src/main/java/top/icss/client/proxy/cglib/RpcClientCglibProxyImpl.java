package top.icss.client.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import top.icss.client.proxy.RpcClientProxy;
import top.icss.register.discover.ServiceDiscovery;

/**
 * @author cd
 * @desc cglib实现
 * @create 2020/5/13 15:51
 * @since 1.0.0
 */
public class RpcClientCglibProxyImpl implements RpcClientProxy {

    /**
     * 服务发现
     */
    private ServiceDiscovery serviceDiscovery;

    /**
     * 协议类型
     */
    private byte protocolType;

    /**
     * 序列化
     */
    private byte serializeType;

    /**
     * 超时
     */
    private int timeout;

    public RpcClientCglibProxyImpl(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public RpcClientCglibProxyImpl(ServiceDiscovery serviceDiscovery, byte serializeType) {
        this.serviceDiscovery = serviceDiscovery;
        this.serializeType = serializeType;
    }

    public RpcClientCglibProxyImpl(ServiceDiscovery serviceDiscovery, byte protocolType, byte serializeType) {
        this.serviceDiscovery = serviceDiscovery;
        this.protocolType = protocolType;
        this.serializeType = serializeType;
    }

    public RpcClientCglibProxyImpl(ServiceDiscovery serviceDiscovery, byte serializeType, int timeout) {
        this.serviceDiscovery = serviceDiscovery;
        this.serializeType = serializeType;
        this.timeout = timeout;
    }

    @Override
    public <T> T getProxyService(Class<T> interfaceClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(new RpcClientCglibProxyHandler(serviceDiscovery, protocolType, serializeType, timeout, interfaceClass));
        Object enhancedObject = enhancer.create();
        return (T)enhancedObject;
    }

}
