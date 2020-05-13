package top.icss.client;

import lombok.extern.slf4j.Slf4j;
import top.icss.entity.ResponsePacket;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author cd
 * @desc
 * @create 2020/4/16 10:54
 * @since 1.0.0
 */
@Slf4j
public class RpcCilentFactory {
    /**
     * 客户端
     */
    private Map<String, RpcClient> rpcClient = new ConcurrentHashMap<String, RpcClient>();

    /**
     * 返回
     */
    private ConcurrentHashMap<String ,LinkedBlockingQueue<ResponsePacket>> resultRes = new ConcurrentHashMap<String, LinkedBlockingQueue<ResponsePacket>>();

    private RpcCilentFactory(){}
    private final static RpcCilentFactory INSTANCE = new RpcCilentFactory();

    /**
     * 单例
     * @return
     */
    public static RpcCilentFactory getInstance(){
        return INSTANCE;
    }


    public void putClient(String key, RpcClient cilent){
        rpcClient.put(key, cilent);
    }

    public RpcClient getClient(String host, int port) throws Exception {
        String key ="/"+host+":"+port;
        if(rpcClient.containsKey(key)){
            return rpcClient.get(key);
        }
        return RpcClient.getInstance().createConnect(host, port);
    }

    public void removeClient(String key){
        if(rpcClient.containsKey(key)){
            rpcClient.remove(key);
        }
    }

    /**
     * 添加消息
     * @param key
     * @param queue
     */
    public void offerResponse(String key, LinkedBlockingQueue<ResponsePacket> queue){
        resultRes.put(key, queue);
    }

    /**
     * 删除消息
     * @param key
     */
    public void removeResponse(String key){
        resultRes.remove(key);
    }

    /**
     * RpcClientHandler 消息调用验证当前请求包是否存在
     * @param response
     * @throws Exception
     */
    public void receiveResponse(ResponsePacket response)throws Exception{
        if(!resultRes.containsKey(response.getId())){
            log.error("give up the response,request id is:" + response.getId() + ",maybe because timeout!");
            return;
        }
        try {

            if(resultRes.containsKey(response.getId())){

                LinkedBlockingQueue<ResponsePacket> queue = resultRes.get(response.getId());
                if (queue != null) {
                    queue.put(response);
                } else {
                    log.warn("give up the response,request id is:"
                            + response.getId() + ",because queue is null");
                }
            }

        } catch (InterruptedException e) {
            log.error("put response error,request id is:" + response.getId(), e);
        }
    }

}
