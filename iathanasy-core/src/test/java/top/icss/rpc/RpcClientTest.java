package top.icss.rpc;

import top.icss.client.RpcCilent;
import top.icss.client.proxy.RpcClientProxy;
import top.icss.client.proxy.jdk.RpcClientJdkProxyImpl;
import top.icss.register.discover.ServiceDiscovery;
import top.icss.register.discover.ZkServiceDiscovery;
import top.icss.rpc.service.HelloService;
import top.icss.rpc.service.TestService;
import top.icss.serializer.SerializerAlgorithm;

/**
 * @author cd
 * @desc 客户端使用
 * @create 2020/4/16 14:56
 * @since 1.0.0
 */
public class RpcClientTest {
    public static void main(String[] args) throws Exception {
        RpcCilent cilent = RpcCilent.getInstance();
        cilent.start();

        ServiceDiscovery serviceDiscovery = new ZkServiceDiscovery();
        RpcClientProxy proxy = new RpcClientJdkProxyImpl(serviceDiscovery, SerializerAlgorithm.DEFAULT);
        HelloService helloService = proxy.getProxyService(HelloService.class);
        for (int i = 0; i < 10; i++) {
            String hello = helloService.hello("World " + i);
            System.out.println(hello);
            Thread.sleep(500);
        }

        TestService testService = proxy.getProxyService(TestService.class);
        for (int i = 0; i < 10; i++) {
            Integer sum = testService.test(i, (i + i));
            System.out.println(sum);
            Thread.sleep(500);
        }

        cilent.stop();

    }
}
