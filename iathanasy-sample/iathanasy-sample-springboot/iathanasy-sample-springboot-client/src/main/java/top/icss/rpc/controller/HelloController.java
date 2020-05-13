package top.icss.rpc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.icss.annotation.RpcAutowired;
import top.icss.rpc.api.HelloService;
import top.icss.rpc.api.TestService;

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
    @RpcAutowired
    private TestService testService;

    @RequestMapping("hello")
    public String hello(String name) {
        try {
            return helloService.hello(name);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping("test")
    public Integer test(Integer x, Integer y) {
       return testService.test(x, y);
    }

    @RequestMapping("avg")
    public int avg(int x, int y) {
        return testService.avg(x, y);
    }

}
