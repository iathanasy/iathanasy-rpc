package top.icss.rpc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.icss.annotation.RpcAutowired;
import top.icss.rpc.api.HelloService;

/**
 * @author cd
 * @desc
 * @create 2020/5/6 17:49
 * @since 1.0.0
 */
@RestController
public class HelloController {

    @RpcAutowired
    private HelloService helloService;

    @RequestMapping("hello")
    public String hello(String name) {
        try {
            return helloService.hello(name);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
