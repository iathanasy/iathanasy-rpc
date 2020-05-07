package top.icss.rpc.service;

import org.springframework.stereotype.Service;
import top.icss.annotation.RpcService;
import top.icss.rpc.api.HelloService;

/**
 * @author cd
 * @desc
 * @create 2020/5/6 15:08
 * @since 1.0.0
 */
@RpcService
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return "Hello "+ msg;
    }
}
