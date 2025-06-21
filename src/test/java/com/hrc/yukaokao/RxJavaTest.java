package com.hrc.yukaokao;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

/**
 * @FileName: RxJavaTest
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/20 18:26
 * @Version: 1.0.0
 */
@SpringBootTest
public class RxJavaTest {

    @Test
    public void rxJavaTest() throws InterruptedException {
        //创建一个流，每秒发送一个地震的整数
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(i -> i + 1)
                .subscribeOn(Schedulers.io());

        // 订阅 Flowable 流，并打印每个接受的数字
        flowable.observeOn(Schedulers.io())
                .doOnNext(item -> System.out.println(item.toString()))
                .subscribe();

        // 让主线程睡眠，以便观察
        Thread.sleep(10000L);
    }
}
