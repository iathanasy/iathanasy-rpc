package top.icss.rpc;

import top.icss.register.ZkServiceRegister;
import top.icss.server.RpcServer;

/**
 * @author cd
 * @desc 暴露服务
 * @create 2020/4/16 14:54
 * @since 1.0.0
 */
public class RpcServerTest {
    public static void main(String[] args) throws Exception {
        RpcServer server = new RpcServer(new ZkServiceRegister(),5893);
        //扫描
        server.init("top.icss.rpc");
        server.start();
    }
}
