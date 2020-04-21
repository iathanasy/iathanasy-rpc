package top.icss.client.proxy.jdk;

import top.icss.client.proxy.RpcClientProxy;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.Proxy;

/**
 * @author cd
 * @desc
 * @create 2020/4/16 11:45
 * @since 1.0.0
 */
public class RpcClientJdkProxyImpl implements RpcClientProxy {

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

    public RpcClientJdkProxyImpl(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public RpcClientJdkProxyImpl(ServiceDiscovery serviceDiscovery, byte serializeType) {
        this.serviceDiscovery = serviceDiscovery;
        this.serializeType = serializeType;
    }

    public RpcClientJdkProxyImpl(ServiceDiscovery serviceDiscovery, byte protocolType, byte serializeType) {
        this.serviceDiscovery = serviceDiscovery;
        this.protocolType = protocolType;
        this.serializeType = serializeType;
    }

    @Override
    public <T> T getProxyService(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new RpcClientJdkProxyHandler(serviceDiscovery, protocolType, serializeType, interfaceClass));
    }
}
