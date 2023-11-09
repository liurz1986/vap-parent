package com.vrv.vap.monitor.agent.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "base.data-batch")
public class BatchQueueProperties {
    private Integer batchTime = 60000;
    private Integer batchCapability = 100;
    private String fileFolder = "/data/monitor";
    private String fileSuffix="txt";
    private String tmpFileSuffix="tmp";
    private Integer maxQueueSize = 30000;

    public Integer getBatchTime() {
        return batchTime;
    }

    public void setBatchTime(Integer batchTime) {
        this.batchTime = batchTime;
    }

    public Integer getBatchCapability() {
        return batchCapability;
    }


    public void setBatchCapability(Integer batchCapability) {
        this.batchCapability = batchCapability;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public void setFileFolder(String fileFolder) {
        this.fileFolder = fileFolder;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getTmpFileSuffix() {
        return tmpFileSuffix;
    }

    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }




    public Integer getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(Integer maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }
}
