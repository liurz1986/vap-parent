package com.vrv.vap.netflow.common.config;

import com.vrv.vap.netflow.common.batch.BatchQueue;
import com.vrv.vap.netflow.common.batch.FileConsumer;
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
    public BatchQueue<Map<String, Object>> batchQueue() {
        BatchQueue<Map<String, Object>> mapBatchQueue = new BatchQueue<>(batchQueueProperties.getBatchCapability(),
                batchQueueProperties.getBatchTime(), fileConsumer, batchQueueProperties.getMaxQueueSize());
        return mapBatchQueue;
    }


}
