package com.vrv.vap.netflow.common.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wh1107066
 * @date 2023/11/29
 */
public class BatchQueueProducer implements Serializable {
    private Logger logger = LoggerFactory.getLogger(BatchQueueProducer.class);
    private final LinkedBlockingQueue<Map<String, Object>> queueList;
    public static final Integer MAX_SIZE = 100000;

    public BatchQueueProducer(LinkedBlockingQueue<Map<String, Object>> queueList) {
        this.queueList = queueList;
    }

    /**
     * 生产消息，往队列中加入消息
     *
     * @param map map
     */
    public void add(Map<String, Object> map) {
        while (queueList.size() == MAX_SIZE) {
            synchronized (queueList) {
                try {
                    logger.warn("Queue is full " + Thread.currentThread().getName() + " is waiting , size: " + queueList.size());
                    queueList.wait();
                } catch (InterruptedException ex) {
                    logger.error("队列已满，InterruptedException", ex);
                }
            }
        }

        // 生产一条消息并通知消费者线程
        synchronized (queueList) {
            try {
                queueList.put(map);
            } catch (InterruptedException e) {
                logger.error("生产失败InterruptedException！！", e);
            }
            queueList.notifyAll();
        }
    }
}