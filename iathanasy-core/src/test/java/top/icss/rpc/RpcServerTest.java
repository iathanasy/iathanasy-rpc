package top.icss.rpc;

import top.icss.memory.DirectMemoryMonitor;
import top.icss.register.ZkServiceRegister;
import top.icss.server.RpcServer;

import java.util.concurrent.TimeUnit;

/**
 * @author cd
 * @desc 暴露服务
 *  1. 先启动 zk
 *  2. 在启动服务 (修改端口可启动多个)
 *  3. 启动客户端 (可多个)
 * @create 2020/4/16 14:54
 * @since 1.0.0
 */
public class RpcServerTest {
    public static void main(String[] args) throws Exception {
        RpcServer server = new RpcServer(new ZkServiceRegister(),5893);
        new DirectMemoryMonitor().startReport();
        //扫描
        server.init("top.icss.rpc");
        server.start();

        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        server.stop();
    }
}
