package top.icss.utils;

import top.icss.annotation.RpcAutowired;
import top.icss.annotation.RpcService;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cd
 * @desc ioc容器
 * @create 2020/4/20 17:12
 * @since 1.0.0
 */
public class IocContainer {
    private Set<Class<?>> clss = new LinkedHashSet<Class<?>>();
    private Map<String, Object> beans = new ConcurrentHashMap<String, Object>();

    public IocContainer(String packageName){
        doScanner(packageName);
        doInstance();
        doAutowired();
    }

    public IocContainer(){}

    public Map<String, Object> getBeans() {
        return beans;
    }

    /**
     * 获取类上 @MyService的注解类
     *
     * @param packageName
     */
    public void doScanner(String packageName) {
        List<Class<Object>> list = ReflectUtils.getClass(packageName);
        for (Class cls : list) {
            boolean service = cls.isAnnotationPresent(RpcService.class);
            if (service) {
                clss.add(cls);
            }
        }
    }

    /**
     * 将class中的类实例化，经key-value：类名（小写）-类对象放入ioc字段中
     */
    public void doInstance() {
        for (Class cls : clss) {
            if (cls.isAnnotationPresent(RpcService.class)) {
                RpcService rpcService = (RpcService) cls.getAnnotation(RpcService.class);
                String beanName = "";
                if(cls.isInterface()){
                    beanName = cls.getName();
                }else {
                    beanName = ("".equals(rpcService.value().trim())) ? toLowerFirstWord(cls.getSimpleName()) : rpcService.value();
                }
                try {

                    Object instance = cls.newInstance();
                    beans.put(beanName, instance);

                    Class[] interfaces = cls.getInterfaces();
                    for (Class<?> i:interfaces){
                        //接口名称
                        beans.put(i.getName(), instance);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 自动化的依赖注入
     */
    public void doAutowired(){
        if(beans.isEmpty()){
            return;
        }

        try {
            Set<Map.Entry<String, Object>> entries = beans.entrySet();
            for (Map.Entry<String, Object> entry: entries){
                Class<?> cls = entry.getValue().getClass();

                Field[] fields = cls.getDeclaredFields();
                //强制获取私有字段
                AccessibleObject.setAccessible(fields,true);
                for (Field f: fields){
                    if(!f.isAnnotationPresent(RpcAutowired.class)){
                        continue;
                    }

                    RpcAutowired rpcAutowired = f.getAnnotation(RpcAutowired.class);
                    String beanName = "";
                    Class icls = f.getType();
                    if(icls.isInterface()){
                        beanName = icls.getName();
                    }else {
                        beanName = ("".equals(rpcAutowired.value().trim())) ? toLowerFirstWord(icls.getName()) : rpcAutowired.value();
                    }
                    //获取当前类实例
                    Object obj = entry.getValue();
                    //容器中获取字段实例
                    Object value = beans.get(beanName);

                    f.set(obj, value);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取实例
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<?> cls){
        RpcService rpcService = cls.getAnnotation(RpcService.class);
        String beanName = "";
        if(cls.isInterface()){
            beanName = cls.getName();
        }else {
            beanName = ("".equals(rpcService.value().trim())) ? toLowerFirstWord(cls.getSimpleName()) : rpcService.value();
        }

        return (T) beans.get(beanName);
    }

    /**
     * 将字符串首字母转换为小写
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
