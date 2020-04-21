package top.icss.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import top.icss.register.ZkConstant;

/**
 * @author cd
 * @desc
 * @create 2020/4/20 15:37
 * @since 1.0.0
 */
public class ZkTest {

    private CuratorFramework client;

    public ZkTest(){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        /*client = CuratorFrameworkFactory.builder()
                .connectString(ZkConstant.ZK_ADDR)
                .sessionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .retryPolicy(retryPolicy).build();*/
        client = CuratorFrameworkFactory.newClient(ZkConstant.ZK_ADDR, ZkConstant.ZK_SESSION_TIMEOUT,ZkConstant.ZK_SESSION_TIMEOUT, retryPolicy);
        client.start();
    }

    public static void main(String[] args) throws Exception {
        ZkTest zk = new ZkTest();
        String s = "top.icss.rpc.service.HelloService";
        String directory = String.format(ZkConstant.ZK_REGISTRY_PATH + "/%s", s);

        PathChildrenCache childrenCache = new PathChildrenCache(zk.client, directory, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {//监听子节点被删除的情
                    String path = event.getData().getPath();
                    String[] nodes = path.split("/");
                    System.out.println("子节点被删除："+ path);

                }else if(event.getType()==PathChildrenCacheEvent.Type.CHILD_ADDED){//监听增加
                    String path = event.getData().getPath();
                    String[] nodes = path.split("/");
                    String[] host = nodes[nodes.length-1].split(":");
                    System.out.println("子节点被增加："+ path);
                }
            }
        });

        System.in.read();

    }
}
