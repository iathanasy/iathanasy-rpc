package top.icss.rpc.service;

import top.icss.annotation.RpcService;

/**
 * @author cd
 * @desc
 * @create 2020/3/16 16:58
 * @since 1.0.0
 */
@RpcService
public class TestServiceImpl implements TestService{

    @Override
    public Integer test(Integer x, Integer y) {
        return x + y;
    }
}
