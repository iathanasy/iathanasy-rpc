package top.icss.client.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import top.icss.client.proxy.AbstractRpcClientProxy;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.Method;

/**
 * @author cd
 * @desc cglib 动态代理
 * @create 2020/5/13 16:23
 * @since 1.0.0
 */
public class RpcClientCglibProxyHandler implements MethodInterceptor {
    private AbstractRpcClientProxy clientProxy;

    public RpcClientCglibProxyHandler(ServiceDiscovery serviceDiscovery,
                                      byte protocolType, byte serializeType , int timeout, Class interfaceClass) {
        //组合模式
        this.clientProxy = new AbstractRpcClientProxy(serviceDiscovery, protocolType, serializeType, timeout, interfaceClass) {};
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        return clientProxy.invokeImpl(methodProxy, method, objects);
    }
}
