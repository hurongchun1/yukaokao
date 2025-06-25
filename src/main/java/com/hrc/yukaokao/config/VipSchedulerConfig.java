package com.hrc.yukaokao.config;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @FileName: VipSchedulerConfig
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/23 13:54
 * @Version: 1.0.0
 */
@Configuration
public class VipSchedulerConfig {
    /**
     * 用户vip的任务调度
     *
     * @return
     */
    @Bean
    public Scheduler vipScheduler() {
        //线程名称
        AtomicInteger threadNumber = new AtomicInteger(0);
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
               Thread thread = new Thread(r,"VIIPThreadPool-"+threadNumber.getAndIncrement());
                //设置为非守护线程，主线程运行完成后，还能继续运行
                thread.setDaemon(false);
                return thread;
            }
        };
        //自己生成线程池
        ExecutorService executorService = new ThreadPoolExecutor(32, 32, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000), threadFactory);
        return Schedulers.from(executorService);
    }
}
