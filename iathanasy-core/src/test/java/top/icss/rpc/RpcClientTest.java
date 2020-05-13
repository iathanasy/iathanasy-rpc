package top.icss.rpc;

import top.icss.client.RpcClient;
import top.icss.client.proxy.RpcClientProxy;
import top.icss.client.proxy.jdk.RpcClientJdkProxyImpl;
import top.icss.register.discover.ServiceDiscovery;
import top.icss.register.discover.ZkServiceDiscovery;
import top.icss.rpc.service.HelloService;
import top.icss.rpc.service.TestService;
import top.icss.serializer.SerializerAlgorithm;

import java.util.concurrent.TimeUnit;

/**
 * @author cd
 * @desc 客户端使用
 * @create 2020/4/16 14:56
 * @since 1.0.0
 *
 */
public class RpcClientTest {

    public static void main(String[] args) throws Exception {
        RpcClient cilent = RpcClient.getInstance();
        cilent.start();
        int count = 10;
        ServiceDiscovery serviceDiscovery = new ZkServiceDiscovery();
        RpcClientProxy proxy = new RpcClientJdkProxyImpl(serviceDiscovery, SerializerAlgorithm.DEFAULT);

        HelloService helloService = proxy.getProxyService(HelloService.class);
        for (int i = 0; i < count; i++) {
            String hello = helloService.hello("World " + i);
            System.out.println(Thread.currentThread().getName() + "::" + hello);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(20 * 1000);

        TestService testService = proxy.getProxyService(TestService.class);
        for (int i = 0; i < count; i++) {
            Integer sum = testService.test(i, (i + i));
            System.out.println(Thread.currentThread().getName() + "::" + sum);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (!Thread.currentThread().isInterrupted()) {
            TimeUnit.HOURS.sleep(1);
        }

        cilent.stop();

    }
}
