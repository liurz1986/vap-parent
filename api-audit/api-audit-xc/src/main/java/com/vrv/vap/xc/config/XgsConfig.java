package com.vrv.vap.xc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * XGS配置实体类
 *
 * @author lil
 */
@Component
@ConfigurationProperties(prefix = "xgs.channel")
public class XgsConfig {
    private int kafkaBatchSize;

    private int retries;

    private int dataBatchSize;

    private String startTime;

    public int getKafkaBatchSize() {
        return kafkaBatchSize;
    }

    public void setKafkaBatchSize(int kafkaBatchSize) {
        this.kafkaBatchSize = kafkaBatchSize;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getDataBatchSize() {
        return dataBatchSize;
    }

    public void setDataBatchSize(int dataBatchSize) {
        this.dataBatchSize = dataBatchSize;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
