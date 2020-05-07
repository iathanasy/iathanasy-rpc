package top.icss.rpc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.icss.memory.DirectMemoryMonitor;
import top.icss.register.ZkServiceRegister;
import top.icss.server.RpcSpringServer;

/**
 * @author cd
 * @desc
 * @create 2020/5/6 17:07
 * @since 1.0.0
 */
@Configuration
public class RpcServerConfig {

    @Value("${rpc.server.port}")
    private int port;

    @Value("${rpc.registry.address}")
    private String address;

    @Bean
    public RpcSpringServer rpcSpringServer(){
        // zk服务注册
        ZkServiceRegister register = new ZkServiceRegister(address);
        // 服务配置 端口
        RpcSpringServer server = new RpcSpringServer(register, port);
        //堆外内存监控
        //new DirectMemoryMonitor().startReport();
        return server;
    }
}
