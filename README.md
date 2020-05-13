
## 简单rpc远程调用
提供无框架和springboot使用案例

## iathanasy-core
rpc核心

## iathanasy-sample
rpc使用test 

### springboot
> 后台启动(iathanasy-admin) top.icss.AdminApplication

> 服务端启动 top.icss.rpc.RpcServerApplication

 1. 启动zookeeper服务
 2. 配置application.yml文件中服务端口和注册中心地址
 3. 分布式启动 修改服务端口为不同即可
 4. 配置启动
 ```
     // zk服务注册
     ZkServiceRegister register = new ZkServiceRegister(address);
     // 服务配置 端口
     RpcSpringServer server = new RpcSpringServer(register, port);
 ```
 
 > 客户端启动 top.icss.rpc.RpcClientApplication
 
 1. 配置application.yml文件中注册中心地址和http端口
 2. 配置客户端启动
```
    RpcClient client = RpcClient.getInstance();
    // zk服务发现
    ServiceDiscovery serviceDiscovery = new ZkServiceDiscovery(address);
    //Rpc客户端代理： jdk代理, cglib代理;
    // protocolType(协议类型): 默认 1 rpc协议
    // serializeType(序列化): 默认 1 Protostuff， 2 java ，3 json
    // timeout(超时)：毫秒
    //RpcClientProxy proxy = new RpcClientJdkProxyImpl(serviceDiscovery, SerializerAlgorithm.JSON, timeout);
    RpcClientProxy proxy = new RpcClientCglibProxyImpl(serviceDiscovery, SerializerAlgorithm.JSON, timeout);

    RpcSpringClientFactory factory = new RpcSpringClientFactory(client, proxy);
``` 

> 访问  

1. [admin](http://127.0.0.1:9507/admin)
2. [hello](http://127.0.0.1:8081/hello?name=World)
3. [test](http://127.0.0.1:8081/test?x=1&y=3)
4. [avg](http://127.0.0.1:8081/avg?x=1&y=8)