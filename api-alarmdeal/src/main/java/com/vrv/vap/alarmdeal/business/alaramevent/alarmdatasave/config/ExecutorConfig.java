package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorConfig {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ExecutorConfig.class);

    public static Executor alarmExtentServiceExecutor(){
        logger.info("ExecutorConfig start");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(6);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setQueueCapacity(32);
        threadPoolTaskExecutor.setThreadNamePrefix("alarm-extend-data-Executor-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    public static Executor riskEventRuleExecutor(){
        logger.info("ExecutorConfig start");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(3);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setQueueCapacity(32);
        threadPoolTaskExecutor.setThreadNamePrefix("risk-event-rule-Executor-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    public static Executor superviseExecutor(){
        logger.info("ExecutorConfig start");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(4);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setQueueCapacity(32);
        threadPoolTaskExecutor.setThreadNamePrefix("supervise-Executor-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
