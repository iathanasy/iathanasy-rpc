package top.icss.register.discover;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.data.Stat;
import top.icss.register.ZkConstant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cd
 * @desc Zk服务发现
 * @create 2020/4/20 9:34
 * @since 1.0.0
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery{

    private List<String> servers = new ArrayList<String>();
    private ConcurrentHashMap<String, Boolean> flag=new ConcurrentHashMap<String, Boolean>();

    private CuratorFramework client;


    public ZkServiceDiscovery(){
        this(ZkConstant.ZK_ADDR);
    }
    /**
     * 启动
     * @param connectionString
     */
    public ZkServiceDiscovery(String connectionString){
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .connectionTimeoutMs(ZkConstant.ZK_SESSION_TIMEOUT)
                .retryPolicy(retryPolicy).build();
        //.newClient(connectionString, ZkConstant.ZK_SESSION_TIMEOUT,ZkConstant.ZK_SESSION_TIMEOUT, retryPolicy);
        client.start();
    }

    @Override
    public String discovery(String serviceName) {
        if(serviceName != null) {
            if(!flag.containsKey(serviceName)){
                try {
                    //监听目录
                    listenServerNode(serviceName);
                } catch (Exception e) {
                    log.error("listenServerNode fail",e);
                }
            }

            String directory = String.format(ZkConstant.ZK_REGISTRY_PATH + "/%s", serviceName);
            Map<String, String> maps = listChildrenDetail(directory);
            if (maps != null && maps.keySet().size() > 0) {
                for (String value : maps.keySet()) {
                    String[] host = value.split(":");
                    servers.add(host[0] + ":" + host[1]);
                }
            }
        }

        int size = servers.size();

        String addr = null;
        if (size == 1) {
            addr = servers.get(0);
        } else if(size > 0) {
            Random r=new Random();
            int j=r.nextInt(servers.size());
            //随机选取
            addr = servers.get(j);
        }

        return addr;
    }


    /**
     * 监听服务节点
     */
    private void listenServerNode(String serviceName) throws Exception {
        String directory = String.format(ZkConstant.ZK_REGISTRY_PATH + "/%s", serviceName);

        PathChildrenCache childrenCache = new PathChildrenCache(client, directory, true);
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {

                if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {//监听子节点被删除的情
                    String path = event.getData().getPath();
                    String[] nodes = path.split("/");
                    if (nodes.length > 0) {
                        String[] host = nodes[nodes.length-1].split(":");
                        updateServerList(host[0] + ":" + host[1]);
                    }

                }else if(event.getType()==PathChildrenCacheEvent.Type.CHILD_ADDED){//监听增加
                    String path = event.getData().getPath();
                    String[] nodes = path.split("/");
                    String[] host = nodes[nodes.length-1].split(":");
                    servers.add(host[0] + ":" + host[1]);
                }
            }
        });

    }

    /**
     * 找到指定节点下所有子节点的名称与值
     * @param node
     * @return
     */
    private Map<String, String> listChildrenDetail(String node){
        Map<String, String> map = Maps.newHashMap();
        try {
            GetChildrenBuilder childrenBuilder =client.getChildren();
            List<String> children = childrenBuilder.forPath(node);
            GetDataBuilder dataBuilder = client.getData();
            if (children != null) {
                for (String child : children) {
                    String propPath = ZKPaths.makePath(node, child);
                    map.put(child, new String(dataBuilder.forPath(propPath), Charsets.UTF_8));
                }
            }
        }
        catch (Exception e) {
            log.error("listChildrenDetail fail",e);
        }
        return map;
    }

    /**
     * 更新本地化的server
     * @param server
     * @throws Exception
     */
    private void updateServerList(String server) throws Exception{
        Iterator<String> iterator = servers.iterator();
        while (iterator.hasNext()){
            if(iterator.next().startsWith(server)){
                iterator.remove();
            }
        }
        /*for(String addr : servers){
            if(addr.startsWith(server)) {//删除
                //deleteNode(addr);
            }
        }*/
    }

    /**
     * 删除节点
     * @param path
     * @throws Exception
     */
    private  void deleteNode(String path) throws Exception {
        // TODO Auto-generated method stub
        try {
            Stat stat = client.checkExists().forPath(path);
            if (stat != null) {
                client.delete().deletingChildrenIfNeeded().forPath(path);
            }
        }catch (Exception e) {
            log.error("deleteNode fail", e);
        }
    }

}
