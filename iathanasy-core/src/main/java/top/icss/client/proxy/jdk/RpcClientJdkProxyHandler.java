package top.icss.client.proxy.jdk;

import lombok.extern.slf4j.Slf4j;
import top.icss.client.proxy.AbstractRpcClientProxy;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author cd
 * @desc jdk动态代理
 * @create 2020/4/16 9:54
 * @since 1.0.0
 */
@Slf4j
public class RpcClientJdkProxyHandler implements InvocationHandler {

    private AbstractRpcClientProxy clientProxy;

    public RpcClientJdkProxyHandler(ServiceDiscovery serviceDiscovery,
                                    byte protocolType, byte serializeType , int timeout, Class interfaceClass) {
        //组合模式
        this.clientProxy = new AbstractRpcClientProxy(serviceDiscovery, protocolType, serializeType, timeout, interfaceClass) {};
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return clientProxy.invokeImpl(proxy, method, args);
    }


}
