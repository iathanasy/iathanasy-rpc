package top.icss.rpc.service;

import org.springframework.stereotype.Service;
import top.icss.annotation.RpcService;
import top.icss.rpc.api.TestService;

/**
 * @author cd
 * @desc
 * @create 2020/3/16 16:58
 * @since 1.0.0
 */
@RpcService
@Service
public class TestServiceImpl implements TestService {

    @Override
    public int avg(int x, int y) {
        return (x + y) / 2;
    }

    @Override
    public Integer test(Integer x, Integer y) {
        return x + y;
    }
}
