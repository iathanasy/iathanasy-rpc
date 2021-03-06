package top.icss.client.proxy.jdk;

import top.icss.client.proxy.RpcClientProxy;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.Proxy;

/**
 * @author cd
 * @desc 只能对接口进行代理。如果要代理的类为一个普通类、没有接口，那么Java动态代理就没法使用了
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

    /**
     * 超时
     */
    private int timeout;

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

    public RpcClientJdkProxyImpl(ServiceDiscovery serviceDiscovery, byte serializeType, int timeout) {
        this.serviceDiscovery = serviceDiscovery;
        this.serializeType = serializeType;
        this.timeout = timeout;
    }

    @Override
    public <T> T getProxyService(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new RpcClientJdkProxyHandler(serviceDiscovery, protocolType, serializeType, timeout, interfaceClass));
    }
}
