package com.vrv.vap.monitor.agent.common;


import com.vrv.vap.monitor.agent.config.BatchQueueProperties;
import com.vrv.vap.monitor.agent.utils.FileUtils;
import org.apache.flume.ChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service
public class FileConsumer implements Consumer<List<String>> {

    private static Logger logger = LoggerFactory.getLogger(FileConsumer.class);

    @Autowired
    private BatchQueueProperties batchQueueProperties;



    @Override
    public void accept(List<String> contents) {
        try {
        String uuid = UUID.randomUUID().toString();
        String tempFileName = uuid +"."+batchQueueProperties.getFileSuffix()+"."+batchQueueProperties.getTmpFileSuffix();
        String tempFilePath = batchQueueProperties.getFileFolder()+File.separator+tempFileName;

        boolean cFlag = FileUtils.createFile(tempFilePath);

        boolean wFlag = FileUtils.writeFile(contents,tempFilePath);
        if(!wFlag){
            //写入文件失败
            logger.error("写入文件失败,file : "+tempFilePath);
            return;
        }
        String fileName = uuid +"."+batchQueueProperties.getFileSuffix();
        String filePath = batchQueueProperties.getFileFolder()+File.separator+fileName;
        FileUtils.reNameFile(tempFilePath,filePath);

        }catch (Exception exception){
            logger.error("消费失败：",exception);

        }

    }





}
