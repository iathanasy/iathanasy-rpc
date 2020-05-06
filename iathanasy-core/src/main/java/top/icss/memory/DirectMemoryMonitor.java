package top.icss.memory;

import io.netty.util.internal.PlatformDependent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.misc.ReflectUtil;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author cd
 * @desc 堆外内存监控
 * @create 2020/4/29 17:42
 * @since 1.0.0
 */
@Slf4j
public class DirectMemoryMonitor {

    private static final int _1k = 1024;
    private static final String key = "netty_direct_memory";

    private AtomicLong directMemory;

    public DirectMemoryMonitor(){
        /*try {
            //1.堆外内存统计字段
            Field field = PlatformDependent.class.getDeclaredField("DIRECT_MEMORY_COUNTER");
            field.setAccessible(true);

            directMemory = (AtomicLong) field.get(PlatformDependent.class);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /**
     * 一秒执行一次
     */
    public void startReport(){
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.scheduleAtFixedRate(this::doReport,0,60, TimeUnit.SECONDS);
    }

    /**
     * 每秒打印一次当前使用的堆外内存
     */
    private void doReport(){
//        int memoryInKb = (int) (directMemory.get() / _1k);
        long memoryInKb = (PlatformDependent.usedDirectMemory() / _1k);
        log.info("{}: {}k",key, memoryInKb);

    }

}
