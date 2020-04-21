package top.icss.rpc.service;

import top.icss.annotation.RpcService;

/**
 * @author cd
 * @desc
 * @create 2020/3/16 16:58
 * @since 1.0.0
 */
@RpcService
public class HelloServiceImpl implements HelloService{

    @Override
    public String hello(String msg) {
        return "Hello "+ msg;
    }
}
