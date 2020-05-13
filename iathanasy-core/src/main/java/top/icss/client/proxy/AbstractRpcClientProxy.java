package top.icss.client.proxy;

import lombok.extern.slf4j.Slf4j;
import top.icss.client.RpcCilentFactory;
import top.icss.client.RpcClient;
import top.icss.entity.RequestPacket;
import top.icss.entity.ResponsePacket;
import top.icss.register.discover.ServiceDiscovery;

import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author cd
 * @desc 抽象客户端代理
 * @create 2020/5/13 15:45
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractRpcClientProxy{

    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;

    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", Object.class);
            toStringMethod = Object.class.getMethod("toString");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }


    protected int proxyHashCode(Object proxy) {
        return System.identityHashCode(proxy);
    }

    protected boolean proxyEquals(Object proxy, Object other) {
        return (proxy == other);
    }

    protected String proxyToString(Object proxy) {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }


    /*************************************proxy********************************************/
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

    /**
     * 超时
     */
    private int timeout;

    private Class interfaceClass;

    public AbstractRpcClientProxy(ServiceDiscovery serviceDiscovery, byte protocolType, byte serializeType , int timeout, Class interfaceClass) {
        this.serviceDiscovery = serviceDiscovery;
        this.protocolType = protocolType;
        this.serializeType = serializeType;
        this.timeout = timeout;
        this.interfaceClass = interfaceClass;
    }

    /**
     * 子类调用
     * @return
     */
    public Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable{
        long beginTime = System.currentTimeMillis();
        RequestPacket request = new RequestPacket();
        request.setInterfaceClassName(interfaceClass.getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setTimestamp(System.currentTimeMillis());

        if(protocolType != 0) {
            request.setProtocolType(protocolType);
        }
        if(serializeType != 0) {
            request.setSerializeType(serializeType);
        }
        if(timeout != 0) {
            request.setTimeout(timeout);
        }

        /**
         * Fix JDK proxy  limitations
         */
        if (hashCodeMethod.equals(method)) {
            return proxyHashCode(proxy);
        }
        if (equalsMethod.equals(method)) {
            return proxyEquals(proxy, args[0]);
        }
        if (toStringMethod.equals(method)) {
            return proxyToString(proxy);
        }
        LinkedBlockingQueue<ResponsePacket> responseQueue = new LinkedBlockingQueue<ResponsePacket>(1);
        RpcCilentFactory.getInstance().offerResponse(request.getId() ,responseQueue);
        // 发现服务
        if (null != serviceDiscovery) {
            serverAddr = serviceDiscovery.discovery(interfaceClass.getName());
        }
        if(serverAddr == null){
            log.error("rpc server is null");
            throw new RuntimeException("rpc server is null");
        }
        String[] hostAndPort = serverAddr.split(":");
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);

        try {
            //发起调用
            RpcClient client = RpcCilentFactory.getInstance().getClient(host, port);
            client.sendRequest(request);
        } catch (Exception e) {
            log.error("send request to os sendbuffer error", e);
            throw new RuntimeException("send request to os send buffer error", e);
        }

        ResponsePacket response = null;
        try {
            response = responseQueue.poll(request.getTimeout() - (System.currentTimeMillis() - beginTime)
                    , TimeUnit.MILLISECONDS);
            log.warn("pool时间 --> ms: " + (System.currentTimeMillis() - beginTime));
        }finally {
            RpcCilentFactory.getInstance().removeResponse(request.getId());
        }
        //结果集超时
        if(response == null &&
                (System.currentTimeMillis() - beginTime) > request.getTimeout()){
            String errorMsg = "receive response timeout("
                    + request.getTimeout() + " ms),server is: "
                    + host + ":" + port
                    + " request id is:" + request.getId();
            log.error(errorMsg);
            response = getResponse(request, response);
            response.setError(new Throwable(errorMsg));
        }else if(response == null &&  (System.currentTimeMillis() - beginTime) >= request.getTimeout()){
            //结果集为空
            response = getResponse(request, response);
        }
        //结果异常
        if (!response.getStatus()) {
            Throwable t = (Throwable) response.getResult();
            //t.fillInStackTrace();
            String errorMsg = "server error,server is: " + port
                    + ":" + port + " request id is:"
                    + request.getId();
            log.error(errorMsg, t);
            //destroy();
            //throw new Exception(errorMsg, t);
            return null;
        }

        return response.getResult();
    }


    /**
     * 赋值 response
     * @param request
     * @param response
     * @return
     */
    private ResponsePacket getResponse(RequestPacket request, ResponsePacket response){
        response = new ResponsePacket();
        response.setId(request.getId());
        response.setVersion(request.getVersion());
        response.setProtocolType(request.getProtocolType());
        response.setSerializeType(request.getSerializeType());
        return response;
    }

}
