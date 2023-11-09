package com.vrv.vap.netflow.common.batch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class BatchQueue<T> {
    private final Logger logger = LoggerFactory.getLogger(BatchQueue.class);
    private final int batchSize;
    private final Consumer<List<T>> consumer;
    private final int timeWaitInMs;

    private final AtomicBoolean isLooping = new AtomicBoolean(false);
    /**
     * 往队列中追加数据，临时数据存储
     */
    private BlockingQueue<T> queue;

    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("netflow-pool-%d").build();
    protected static ExecutorService executorService = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    private AtomicLong start = new AtomicLong(System.currentTimeMillis());

    /**
     * 启动时初始化queue队列
     *
     * @param batchSize    一次批处理最大处理条数 默认：200，越大越消耗内存
     * @param timeWaitInMs 最长等待时间 timeout毫秒值 60000
     * @param consumer     消费者
     * @param maxSize      对接中存储的最大数据长度 40000
     */
    public BatchQueue(int batchSize, int timeWaitInMs, Consumer<List<T>> consumer, int maxSize) {
        this.batchSize = batchSize;
        this.timeWaitInMs = timeWaitInMs;
        this.consumer = consumer;
        queue = new LinkedBlockingQueue<>(maxSize);
    }

    /**
     * add方法往队列中追加数据
     * 第一次 compareAndSet()的原子性操作，由false变为true
     *
     * @param t 泛型
     * @return true
     */
    public boolean add(T t) {
        boolean result = queue.add(t);
        //logger.info(String.format("BatchQueue数据加入队列,isLooping: %s", isLooping.get()));
        if (!isLooping.get() && result) {
            synchronized (isLooping) {
                try {
                    if (!isLooping.get()) {
                        logger.info("再次启动线程，停止的running!");
                        // executorService.execute(new ExeThread());
                        new Thread(new ExeThread(), "batchQueueConsumerThread").start();
                        isLooping.compareAndSet(false, true);
                    }
                } catch (Exception e) {
                    logger.error("add throw new RuntimeException", e);
                }
            }
        }
        return result;
    }


    private synchronized void drainToConsume() {
        List<T> drained = new Vector<>();
        int total = queue.size();
        int num = queue.drainTo(drained, batchSize);
        logger.info("queue total size:" + total + ", consume消费queue队列中的size:" + num);
        if (num > 0) {
            consumer.accept(drained);
            start.set(System.currentTimeMillis());
        }
    }

    /**
     * 提供线程消费queue队列数据
     */
    class ExeThread implements Runnable {
        @Override
        public void run() {
//            logger.info("ExeThread run");
            while (true) {
                try {
                    long last = System.currentTimeMillis() - start.get();
//                    logger.info(String.format("ExeThread执行queueSize:%s, batchSize:%s,  时间last：%s, timeWaitInMs:%s , isLooping: %s", queue.size(), batchSize, last, timeWaitInMs, isLooping.get()));
                    if (queue.size() >= batchSize || (!queue.isEmpty() && last > timeWaitInMs)) {
                        logger.info(String.format("ExeThread进入queue并消费queue.size：%s, batchSize:%s , isLooping: %s", queue.size(), batchSize, isLooping.get()));
                        drainToConsume();
                    } else if (queue.isEmpty()) {
                        logger.info("queue is null and break!");
                        isLooping.set(false);
                        break;
                    } else {
                        Thread.sleep(timeWaitInMs / 5);
                    }
                } catch (Exception e) {
                    logger.error("batchQueue异常！", e);
                }
            }
        }
    }
}
