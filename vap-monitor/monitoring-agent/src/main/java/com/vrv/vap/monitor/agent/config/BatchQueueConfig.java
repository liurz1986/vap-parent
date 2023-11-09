package com.vrv.vap.monitor.agent.config;


import com.vrv.vap.monitor.agent.common.BatchQueue;
import com.vrv.vap.monitor.agent.common.FileConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class BatchQueueConfig {


    @Autowired
    private BatchQueueProperties batchQueueProperties;

    @Autowired
    private FileConsumer fileConsumer;

    @Bean
    public BatchQueue<String> batchQueue() {
        BatchQueue<String> mapBatchQueue = new BatchQueue<String>(batchQueueProperties.getBatchCapability(),batchQueueProperties.getBatchTime(),fileConsumer,batchQueueProperties.getMaxQueueSize());
        return mapBatchQueue;
    }


}
