package top.icss.rpc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.icss.client.RpcClient;
import top.icss.client.RpcSpringClientFactory;
import top.icss.client.proxy.RpcClientProxy;
import top.icss.client.proxy.jdk.RpcClientJdkProxyImpl;
import top.icss.register.discover.ServiceDiscovery;
import top.icss.register.discover.ZkServiceDiscovery;
import top.icss.serializer.SerializerAlgorithm;

/**
 * @author cd
 * @desc
 * @create 2020/5/6 17:02
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RpcClientConfig {

    @Value("${rpc.registry.address}")
    private String address;

    @Bean
    public RpcSpringClientFactory clientFactory(){
        RpcClient client = RpcClient.getInstance();
        // zk服务发现
        ServiceDiscovery serviceDiscovery = new ZkServiceDiscovery(address);
        //Rpc客户端代理： 默认jdk代理;
        // protocolType(协议类型): 默认 1 rpc协议
        // serializeType(序列化): 默认 1 Protostuff， 2 java ，3 json
        RpcClientProxy proxy = new RpcClientJdkProxyImpl(serviceDiscovery, SerializerAlgorithm.JSON);

        RpcSpringClientFactory factory = new RpcSpringClientFactory(client, proxy);
        log.info(">>>>>>>>>>> rpc invoker config init finish.");
        return factory;
    }
}
