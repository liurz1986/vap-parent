package com.vrv.vap.netflow.common.batch;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author wh1107066
 * @date 2023/11/29
 */

@Component
@Order(value = 101)
public class ConsumerCommandLineRunner implements CommandLineRunner {
    private static final ThreadFactory NAMEDTHREADFACTORY = new ThreadFactoryBuilder().setNameFormat("netflow-pool-%d").build();
    protected static ExecutorService executorService = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), NAMEDTHREADFACTORY, new ThreadPoolExecutor.AbortPolicy());

    @Autowired
    private ReceivedLinkedBlockingQueue receivedLinkedBlockingQueue;

    @Autowired
    private SendFlumeDataConsumer sendFlumeDataConsumer;

    /**
     * 系统激动实例化两个消费者
     * @param args 参数
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        executorService.execute(new BatchQueueConsumer(receivedLinkedBlockingQueue.getQueueList(), sendFlumeDataConsumer));
        executorService.execute(new BatchQueueConsumer(receivedLinkedBlockingQueue.getQueueList(), sendFlumeDataConsumer));
    }
}
