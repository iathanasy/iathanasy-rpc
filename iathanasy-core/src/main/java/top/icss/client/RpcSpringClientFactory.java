package top.icss.client;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.icss.annotation.RpcAutowired;
import top.icss.client.proxy.RpcClientProxy;

import java.lang.reflect.Field;

/**
 * @author cd
 * @desc for spring
 * @create 2020/5/6 17:16
 * @since 1.0.0
 */
@Slf4j
@Data
public class RpcSpringClientFactory implements ApplicationContextAware, BeanPostProcessor, DisposableBean {

    private ApplicationContext ctx;

    /**需要set**/
    private RpcClientProxy rpcProxy;
    private RpcClient cilent;

    public RpcSpringClientFactory(RpcClient cilent, RpcClientProxy rpcProxy) {
        this.cilent = cilent;
        this.rpcProxy = rpcProxy;
    }
    private RpcSpringClientFactory(){}

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
        cilent.start();
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);

            RpcAutowired rpcAutowired = field.getAnnotation(RpcAutowired.class);
            if (null == rpcAutowired) {
                continue;
            }

            Class<?> fieldType = field.getType();
            if (!fieldType.isInterface()) {
                log.error("RPC服务的属性不能接口类型:{}，自动注入失败", field.getName());
                continue;
            }

            Object proxyObj = rpcProxy.getProxyService(fieldType);

            try {
                field.set(bean, proxyObj);
            } catch (IllegalAccessException e) {
                log.error("属性[{}]注入代理实例失败:", field.getName(), e);
            }

        }

        return bean;
    }

    @Override
    public void destroy() throws Exception {
        cilent.stop();
    }
}
