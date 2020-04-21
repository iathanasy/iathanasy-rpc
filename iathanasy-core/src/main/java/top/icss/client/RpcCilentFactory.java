package top.icss.client;

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
public class RpcCilentFactory {
    /**
     * 客户端
     */
    private Map<String, RpcCilent> rpcClient = new ConcurrentHashMap<String, RpcCilent>();

    /**
     * 返回
     */
    private BlockingQueue<ResponsePacket> resultRes = new LinkedBlockingQueue<ResponsePacket>();

    private RpcCilentFactory(){}
    private final static RpcCilentFactory INSTANCE = new RpcCilentFactory();

    /**
     * 单例
     * @return
     */
    public static RpcCilentFactory getInstance(){
        return INSTANCE;
    }


    public void putClient(String key, RpcCilent cilent){
        rpcClient.put(key, cilent);
    }

    public RpcCilent getClient(String host, int port) throws Exception {
        String key ="/"+host+":"+port;
        if(rpcClient.containsKey(key)){
            return rpcClient.get(key);
        }
        return RpcCilent.getInstance().createConnect(host, port);
    }

    public void removeClient(String key){
        if(rpcClient.containsKey(key)){
            rpcClient.remove(key);
        }
    }

    public void offer(ResponsePacket response){
        resultRes.offer(response);
    }

    public ResponsePacket task()throws Exception{
        return resultRes.take();
    }

}
