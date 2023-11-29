package com.vrv.vap.netflow.common.batch;

import com.vrv.vap.netflow.common.config.BatchQueueProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wh1107066
 * @date 2023/11/29
 */
public class ReceivedLinkedBlockingQueue implements InitializingBean, Serializable {

    @Autowired
    private BatchQueueProperties batchQueueProperties;

    private LinkedBlockingQueue<Map<String, Object>> queueList;

    @Override
    public void afterPropertiesSet() throws Exception {
        queueList = new LinkedBlockingQueue<>(batchQueueProperties.getMaxQueueSize());
    }

    public LinkedBlockingQueue<Map<String, Object>> getQueueList() {
        return queueList;
    }

    public void setQueueList(LinkedBlockingQueue<Map<String, Object>> queueList) {
        this.queueList = queueList;
    }
}
