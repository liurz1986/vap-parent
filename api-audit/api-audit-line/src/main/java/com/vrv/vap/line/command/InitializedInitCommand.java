package com.vrv.vap.line.command;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.vrv.vap.toolkit.config.PathConfig;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.line.client.ElasticSearchManager;
import com.vrv.vap.line.config.IndexConfig;
import com.vrv.vap.line.init.IndexCache;
import com.vrv.vap.line.schedule.TaskLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 初始化执行
 */
@Component
public class InitializedInitCommand implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(InitializedInitCommand.class);

    @Autowired(required = false)
    private NacosAutoServiceRegistration registration;

    @Value("${server.port}")
    Integer port;

    @Autowired
    private PathConfig pathConfig;

    @Autowired
    private TaskLoader taskLoader;

    @Autowired
    private ElasticSearchManager client;

    @Autowired
    private IndexConfig indexConfig;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        taskLoader.start();
        PathTools.setPathConfig(pathConfig);
        registNacos(args);
        IndexCache.cache(client, indexConfig);

    }

    public void registNacos(String... args) {
        if (registration != null && port != null) {
            Integer tomcatPort = port;
            registration.setPort(tomcatPort);
            registration.start();
            log.info("nacos register success!");
        }
    }
    /**
     * 定时更新es索引缓存
     * 每天00:01:00执行一次
     */
    @Scheduled(cron = "0 01 0 * * ?")
    public void updateAssetCsv(){
        log.info("定时更新es索引缓存!");
        IndexCache.cache(client, indexConfig);
    }
}
