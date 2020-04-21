package top.icss.client.proxy.jdk;

import lombok.extern.slf4j.Slf4j;
import top.icss.client.RpcCilent;
import top.icss.client.RpcCilentFactory;
import top.icss.entity.RequestPacket;
import top.icss.entity.ResponsePacket;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author cd
 * @desc jdk动态代理
 * @create 2020/4/16 9:54
 * @since 1.0.0
 */
@Slf4j
public class RpcClientJdkProxyHandler implements InvocationHandler {

    /**
     * 服务器地址
     */
    private String serverAddr;
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

    private Class interfaceClass;

    public RpcClientJdkProxyHandler(ServiceDiscovery serviceDiscovery, byte protocolType, byte serializeType, Class interfaceClass) {
        this.serviceDiscovery = serviceDiscovery;
        this.protocolType = protocolType;
        this.serializeType = serializeType;
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long beginTime = System.currentTimeMillis();

        RequestPacket request = new RequestPacket();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceClassName(interfaceClass.getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        if(protocolType != 0) {
            request.setProtocolType(protocolType);
        }
        if(serializeType != 0) {
            request.setSerializeType(serializeType);
        }

        // 发现服务
        if (null != serviceDiscovery) {
            serverAddr = serviceDiscovery.discovery(interfaceClass.getName());
        }

        String[] hostAndPort = serverAddr.split(":");
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);


        ResponsePacket response = null;
        try {
            RpcCilent client = RpcCilentFactory.getInstance().getClient(host, port);
            client.sendRequest(request);

            response = RpcCilentFactory.getInstance().task();
        } catch (Exception e) {
            log.error("send request to os sendbuffer error", e);
            throw new RuntimeException("send request to os sendbuffer error", e);
        }
        log.warn("pool时间 --> ms: "+ (System.currentTimeMillis() - beginTime));
        return response.getResult();
    }
}
