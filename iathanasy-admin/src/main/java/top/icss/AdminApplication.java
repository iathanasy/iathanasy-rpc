package top.icss;

import top.icss.netty.HttpServer;
import top.icss.zk.Zk;

/**
 * @author cd
 * @desc http://127.0.0.1:9507/admin
 * @create 2020/5/12 16:55
 * @since 1.0.0
 */
public class AdminApplication {
    public static void main(String[] args) throws Exception {
        Zk zk = new Zk();
        HttpServer server = new HttpServer(zk);
        server.start();
    }
}
