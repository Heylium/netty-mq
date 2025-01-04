package com.helium.nettymq.broker.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 通用的线程池配置
 */
public class CommonThreadPoolConfig {

    // 专门用于将topic配置异步刷盘使用
    public static ThreadPoolExecutor refreshMqTopicExecutor = new ThreadPoolExecutor(
            1,
            1,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("refresh-mq-topic-config");
                return thread;
            }
    );
}
