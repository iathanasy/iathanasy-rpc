package top.icss.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import top.icss.mode.ServiceModel;
import top.icss.zk.Zk;

import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/5/12 16:59
 * @since 1.0.0
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Zk zk;
    private static final String ServerName = "netty/1.0";

    public HttpServerHandler(Zk zk) {
        this.zk = zk;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        readRequest(request);

        String sendMsg;
        //验证服务请求路径
        String uri = request.uri();
        switch (uri) {
            case "/":
                sendMsg = "<h3>Netty HTTP Server</h3><p>Hello Word!</p>";
                break;
            case "/admin":
                List<ServiceModel> serviceModels = zk.serviceList();
                sendMsg = JSONObject.toJSONString(serviceModels);
                break;
            default:
                sendMsg = "<h3>Netty HTTP Server</h3><p>I was lost!</p>";
                break;
        }

        writeResponse(ctx, sendMsg);
    }

    /**
     * 输出
     * @param ctx
     * @param msg
     */
    private void writeResponse(ChannelHandlerContext ctx, String msg) {
        ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

        FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf);

        res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
        res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        setServer(res);
        ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    /**
     * 请求消息
     * @param msg
     */
    private void readRequest(FullHttpRequest msg) {
        log.warn("======请求行======");
        log.warn(msg.method() + " " + msg.uri() + " " + msg.protocolVersion());

        log.warn("======请求头======");
        for (String name : msg.headers().names()) {
            log.warn(name + ": " + msg.headers().get(name));

        }

        log.warn("======消息体======");
        log.warn(msg.content().toString(CharsetUtil.UTF_8));

    }


    /**
     * 设置服务名称
     * @param response
     */
    private void setServer(HttpResponse response){
        response.headers().set(HttpHeaderNames.SERVER, ServerName);
    }
}
