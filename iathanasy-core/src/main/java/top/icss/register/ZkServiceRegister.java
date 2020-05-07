package top.icss.register;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import top.icss.utils.NetUtil;

import java.util.concurrent.CountDownLatch;

/**
 * @author cd
 * @desc 默认服务注册 zk
 * @create 2020/4/10 15:20
 * @since 1.0.0
 */
@Slf4j
public class ZkServiceRegister implements ServiceRegister{
    private CuratorFramework client;

    public ZkServiceRegister(){
        this(ZkConstant.ZK_ADDR);
    }
    /**
     * 启动
     * @param connectionString
     */
    public ZkServiceRegister(String connectionString){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .retryPolicy(retryPolicy).build();
                //.newClient(connectionString, ZkConstant.ZK_SESSION_TIMEOUT,ZkConstant.ZK_SESSION_TIMEOUT, retryPolicy);
        client.start();
    }

    /**
     * 服务注册
     * @param metadata
     */
    @Override
    public void register(RegisterMeta metadata) {
        if (metadata == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        /*String directory = String.format(ZkConstant.ZK_REGISTRY_PATH + "/%s/%s/%s",
                metadata.getGroup(),
                metadata.getServiceProviderName(),
                metadata.getVersion());*/
        String directory = String.format(ZkConstant.ZK_REGISTRY_PATH + "/%s", metadata.getServiceProviderName());
        try {
            if (null == client.checkExists().forPath(directory)) {
                // 创建一个 允许所有人访问的 持久节点
                client.create()
                        .creatingParentsIfNeeded()//递归创建,如果没有父节点,自动创建父节点
                        .withMode(CreateMode.PERSISTENT)//节点类型,持久节点
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)//设置ACL,和原生API相同
                        .forPath(directory);
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Create parent path failed, directory: {}, {}.", directory, e);
            }
        }
        String address = NetUtil.getLocalAddress();
        metadata.setHost(address);

        try {
            String nodePath = String.format("%s/%s:%s:%s",
                            directory,
                            metadata.getHost(),
                            String.valueOf(metadata.getPort()),
                            String.valueOf(metadata.getWeight()));
            //在客户端断开连接时，znode将被删除。临时节点
            client.create()
                    .withMode(CreateMode.EPHEMERAL) //节点类型,临时节点
                    .forPath(nodePath, address.getBytes());

        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Create register meta: {} path failed, {}.", metadata, e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch downLatch = new CountDownLatch(1);
        RegisterMeta meta = new RegisterMeta();
        meta.setPort(5891);
        meta.setGroup("test");
        meta.setServiceProviderName(ServiceRegister.class.getName());
        meta.setVersion("1.0");
        meta.setWeight(50);

        ZkServiceRegister zk = new ZkServiceRegister(ZkConstant.ZK_ADDR);
        zk.register(meta);
        downLatch.await();

    }
}
