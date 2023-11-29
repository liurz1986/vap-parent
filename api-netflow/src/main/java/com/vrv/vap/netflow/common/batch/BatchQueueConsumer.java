package com.vrv.vap.netflow.common.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * @author wh1107066
 * @date 2023/11/29
 */
public class BatchQueueConsumer implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(BatchQueueConsumer.class);
    private final LinkedBlockingQueue<Map<String, Object>> queueList;
    private static final int BATCH_SIZE = 1000;
    private final Consumer<List<Map<String, Object>>> consumer;

    public BatchQueueConsumer(LinkedBlockingQueue<Map<String, Object>> queueList, Consumer<List<Map<String, Object>>> consumer) {
        this.queueList = queueList;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (queueList) {
                // 如果消息队列为空则等待
                while (queueList.isEmpty()) {
                    System.out.println("Queue is empty " + Thread.currentThread().getName() + " is waiting , size is " + queueList.size());
                    try {
                        queueList.wait();
                    } catch (InterruptedException e) {
                        logger.error("消费失败InterruptedException", e);
                    }
                }

                // 消费消息
                try {
                    List<Map<String, Object>> drained = new Vector<>();
                    int num = queueList.drainTo(drained, BATCH_SIZE);
                    logger.debug("queue total size:" + queueList.size() + ", consume消费queue队列中的size:" + num);
                    if (num > 0) {
                        consumer.accept(drained);
                    }
                } catch (Exception e) {
                    logger.error("消费对接中的数据失败!", e);
                } finally {
                    // 通知生产者线程
                    queueList.notifyAll();
                }
            }
        }
    }
}
