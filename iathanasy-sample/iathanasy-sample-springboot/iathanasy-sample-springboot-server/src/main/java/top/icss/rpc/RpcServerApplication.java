package top.icss.rpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author cd
 * @desc 服务启动
 *  1. 启动zookeeper服务
 *  2. 配置application.yml文件中服务端口和注册中心地址
 *  3. 分布式启动 修改服务端口为不同即可
 * @create 2020/5/6 17:07
 * @since 1.0.0
 */
@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcServerApplication.class, args);
    }
}
