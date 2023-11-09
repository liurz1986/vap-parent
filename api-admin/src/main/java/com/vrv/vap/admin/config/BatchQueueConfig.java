package com.vrv.vap.admin.config;

import com.vrv.vap.admin.common.batch.BatchQueue;
import com.vrv.vap.admin.common.batch.FileConsumer;
import com.vrv.vap.admin.common.config.BatchQueueProperties;
import com.vrv.vap.admin.model.User;
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
    public BatchQueue<Map> batchQueue() {
        BatchQueue<Map> mapBatchQueue = new BatchQueue<>(batchQueueProperties.getBatchCapability(),batchQueueProperties.getBatchTime(),fileConsumer);
        return mapBatchQueue;
    }

}
