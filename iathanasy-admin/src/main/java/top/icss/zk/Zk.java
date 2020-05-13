package top.icss.zk;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import top.icss.mode.ServiceModel;
import top.icss.mode.ServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/5/12 17:19
 * @since 1.0.0
 */
@Slf4j
public class Zk {

    private CuratorFramework client;

    public Zk() {
        this(ZkConstant.ZK_ADDR);
    }

    /**
     * 初始化 zk客户端
     * @param connectionString
     */
    public Zk(String connectionString){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory
                .newClient(connectionString, ZkConstant.ZK_SESSION_TIMEOUT,ZkConstant.ZK_SESSION_TIMEOUT, retryPolicy);
        client.start();
    }

    /**
     * 获取注册服务列表
     * @return
     */
    public List<ServiceModel> serviceList() throws Exception {
        List<String> services = client.getChildren().forPath(ZkConstant.ZK_REGISTRY_PATH);
        services.stream().forEach(System.out::println);

        final List<ServiceModel> serviceModels = new ArrayList<ServiceModel>();
        for (String serviceName : services) {
            ServiceModel serviceModel = new ServiceModel();
            serviceModel.setServiceName(serviceName);
            List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();

            List<String> serverPayLoadList = client.getChildren().forPath(ZkConstant.ZK_REGISTRY_PATH + "/" + serviceName);
            serverPayLoadList.stream().forEach(System.out::println);

            for (String serverPayLoad : serverPayLoadList) {
                ServiceProvider serviceProvider = new ServiceProvider();
                List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(serverPayLoad);
                serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));
                serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));
                serviceProviders.add(serviceProvider);
            }
            serviceModel.setServiceProviders(serviceProviders);

            serviceModels.add(serviceModel);
        }
        return serviceModels;
    }

    /**
     * 服务器列表
     * @param service 注册的服务
     * @return
     */
    public List<String> serverList(String service) throws Exception {
        List<String> serverList = client.getChildren().forPath(ZkConstant.ZK_REGISTRY_PATH + "/" + service);
        serverList.stream().forEach(System.out::println);
        return serverList;
    }
}
