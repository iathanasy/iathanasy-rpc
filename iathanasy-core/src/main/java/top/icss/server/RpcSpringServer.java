package top.icss.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.icss.annotation.RpcService;
import top.icss.register.ServiceRegister;

import java.util.Map;

/**
 * @author cd
 * @desc for spring
 * @create 2020/5/6 16:06
 * @since 1.0.0
 */
public class RpcSpringServer extends RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean {


    public RpcSpringServer(ServiceRegister serviceRegistry) {
        super(serviceRegistry);
    }

    public RpcSpringServer(ServiceRegister serviceRegistry, int port) {
        super(serviceRegistry, port);
    }

    /**
     * Spring容器初始化后将所有使用了RpcService注解的类进行记录
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                // valid
                if (serviceBean.getClass().getInterfaces().length ==0) {
                    throw new RuntimeException("service(RpcService) must inherit interface.");
                }
                // add service
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                Class cls = serviceBean.getClass();
                String beanName = cls.getInterfaces()[0].getName();
                serviceMap.put(beanName, serviceBean);
            }
        }
    }


    /**
     * Bean初始化完成后
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        super.start();
    }


    /**
     * 销毁
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        super.stop();
    }
}
