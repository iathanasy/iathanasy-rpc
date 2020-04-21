package top.icss.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import top.icss.entity.RequestPacket;
import top.icss.entity.ResponsePacket;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cd
 * @desc
 * @create 2020/4/9 17:40
 * @since 1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<RequestPacket> {
    public static final RpcServerHandler INSTANCE = new RpcServerHandler();

    private static final AtomicInteger channelCounter = new AtomicInteger(0);

    private Map<String, Object> beans;

    public RpcServerHandler setBeans(Map<String, Object> beans) {
        this.beans = beans;
        return this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int count = channelCounter.incrementAndGet();
        log.info("Connects with {} as the {} th channel.", ctx.channel(), count);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        int count = channelCounter.getAndDecrement();
        log.warn("Disconnects with {} as the {} th channel.", ctx.channel(), count);
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestPacket request) throws Exception {
            ResponsePacket response = new ResponsePacket();
            response.setId(request.getId());
            response.setVersion(request.getVersion());
            response.setSerializeType(request.getSerializeType());
            response.setProtocolType(request.getProtocolType());
            try {
            // 处理并设置返回结果
            Object result = invoke(request);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }
        ctx.writeAndFlush(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("Exception：" + cause.getMessage());
    }

    /**
     * 反射调用
     * @param request
     * @return
     * @throws Exception
     */
    private Object invoke(RequestPacket request) throws Exception {
        if (beans == null){
            throw new IllegalArgumentException("beans instance == null");
        }

        String className = request.getInterfaceClassName();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();

        Class[] parameterTypes = new Class[parameters.length];
        for (int i = 0, length = parameters.length; i < length; i++) {
            parameterTypes[i] = parameters[i].getClass();
        }

        log.warn("服务端开始调用--> {}", request);

        Object serviceBean = beans.get(className);
        Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);

        Object result = method.invoke(serviceBean, parameters);
        return result;
    }
}
