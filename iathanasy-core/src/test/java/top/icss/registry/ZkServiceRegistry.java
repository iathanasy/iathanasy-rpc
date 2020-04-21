package top.icss.registry;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

/**
 * @author cd
 * @desc
 * @create 2020/4/17 9:16
 * @since 1.0.0
 */
@Slf4j
public class ZkServiceRegistry implements Watcher{

    public static String ZK_ADDRS = "127.0.0.1:2181";
    public static final Integer timeout = 5000;

    public static final String ZK_PATH = "/iathanasy";

    private  ZooKeeper zooKeeper;

    public ZkServiceRegistry() {}

    {
        try {
            zooKeeper = new ZooKeeper(ZK_ADDRS, timeout, this);
        } catch (IOException e) {
            e.printStackTrace();
            if (zooKeeper != null) {
                try {
                    zooKeeper.close();
                } catch (InterruptedException e1){
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 创建节点
     * @param path 创建的路径
     * @param data 存储的数据的byte[]

     * acl：控制权限策略
     *      Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
     *      CREATOR_ALL_ACL --> auth:user:password:cdrwa
     * createMode: 节点类型, 是一个枚举
     *      PERSISTENT：持久节点
     *      PERSISTENT_SEQUENTIAL：持久顺序节点
     *      EPHEMERAL：临时节点
     *      EPHEMERAL_SEQUENTIAL：临时顺序节点
     */
    public void createZKNode(String path, byte[] data) throws Exception {
        //检查父节点并创建
        checkParent(path);

        String currPath = zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        log.warn("created zookeeper node ({} => {})", currPath, data);
    }

    /**
     * 检查父目录是否创建
     * @param path
     */
    public void checkParent(String path) throws Exception {
        if(null == zooKeeper.exists(path, false)){
            int index = path.lastIndexOf("/");
            if(index < 0){
                return;
            }

            String parentPath = path.substring(0, index);

            checkParent(parentPath);

            if(null == zooKeeper.exists(parentPath, false)) {
                zooKeeper.create(parentPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * 删除节点
     * @param path
     * @param  version 预期的匹配版本(每次修改会自动累加)
     */
    public void deleteZkNode(String path, int version) throws Exception {
        zooKeeper.delete(path, version);
    }

    /**
     *  修改节点
     * @param path 节点路径
     * @param data 数据
     * @param version 预期的匹配版本(每次修改会自动累加)
     */
    public void updateZkNode(String path, byte[] data, int version) throws Exception {
        Stat stat = zooKeeper.setData(path, data, version);
        log.warn("stat--> {}", stat);
    }

    /**
     * path：节点路径
     * watch：自定义watcher/true或者false，注册一个watch事件
     * stat：节点的数据和统计信息
     * DataCallback：用于检索节点的数据和统计信息的回调函数
     * @param path
     */
    public void selectZkNode(String path) throws Exception{
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(path, true, stat);
        log.warn("data---> {}, stat---> {}", new String(data,"UTF-8"), stat);
    }



    @Test
    public void testConnectZk() throws Exception {
        ZkServiceRegistry registry = new ZkServiceRegistry();

        long sessionId = zooKeeper.getSessionId();
        String ssid = "0x" + Long.toHexString(sessionId);
        System.out.println(ssid);
        byte[] sessionPassword = zooKeeper.getSessionPasswd();

        log.warn("客户端开始连接zookeeper服务器...");
        log.warn("连接状态:{}",zooKeeper.getState());
        Thread.sleep(1000);
        log.warn("连接状态:{}",zooKeeper.getState());

        new Thread().sleep(200);

        // 开始会话重连
        log.warn("开始会话重连...");

        ZooKeeper zkSession = new ZooKeeper(ZK_ADDRS,
                timeout,
                registry,
                sessionId,
                sessionPassword);
        log.warn("重新连接状态zkSession：{}", zkSession.getState());
        new Thread().sleep(1000);
        log.warn("重新连接状态zkSession：{}", zkSession.getState());
    }

    @Test
    public void testCreateZKNode() throws Exception {
        ZkServiceRegistry registry = new ZkServiceRegistry();
        String addr = InetAddress.getLocalHost().getHostAddress();
        System.out.println(addr);
        registry.createZKNode(ZK_PATH +"/registry", addr.getBytes());
    }

    @Test
    public void testSelectZKNode() throws Exception {
        ZkServiceRegistry registry = new ZkServiceRegistry();
        registry.selectZkNode(ZK_PATH);

        // 同步获得结果
        List<String> childrenList = zooKeeper.getChildren(ZK_PATH, true);
        System.out.println("同步getChildren获得数据结果：" + childrenList);
    }

    @Test
    public void testUpdateZKNode() throws Exception {
        ZkServiceRegistry registry = new ZkServiceRegistry();
        String addr = InetAddress.getLocalHost().getHostAddress() + ":10010";
        registry.updateZkNode(ZK_PATH, addr.getBytes(), 0);
    }

    @Test
    public void testDeleteZKNode() throws Exception {
        ZkServiceRegistry registry = new ZkServiceRegistry();
        registry.deleteZkNode(ZK_PATH, 1);
    }



    @Override
    public void process(WatchedEvent event) {
        try {
            if (Event.KeeperState.SyncConnected == event.getState()) {
                if (Event.EventType.None == event.getType() && null == event.getPath()) {
                    System.out.println("none znode。");
                } else if (Event.EventType.NodeCreated == event.getType()) {
                    System.out.println("success create znode: " + event.getPath());
                    zooKeeper.exists(event.getPath(), true);
                } else if (Event.EventType.NodeDeleted == event.getType()) {
                    System.out.println("success delete znode: " + event.getPath());
                    zooKeeper.exists(event.getPath(), true);
                } else if (Event.EventType.NodeDataChanged == event.getType()) {
                    System.out.println("data changed of znode: " + event.getPath());
                    zooKeeper.exists(event.getPath(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.warn("接收到watch通知:{}",event);
    }
}
