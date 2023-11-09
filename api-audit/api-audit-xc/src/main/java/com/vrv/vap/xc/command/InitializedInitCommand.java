package com.vrv.vap.xc.command;

import com.alibaba.cloud.nacos.registry.NacosAutoServiceRegistration;
import com.vrv.vap.toolkit.config.PathConfig;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.xc.client.ElasticSearchManager;
import com.vrv.vap.xc.config.IndexConfig;
import com.vrv.vap.xc.init.IndexCache;
import com.vrv.vap.xc.schedule.TaskLoader;
import com.vrv.vap.xc.tools.BaseAreaTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
        //部署到东方通等容器时必须手动注册到nacos
        taskLoader.start();
        registNacos(args);

        // -----------------------初始化-----------------------
        PathTools.setPathConfig(pathConfig);

        Export.startWatcher();
        BaseAreaTools.startOnce();


//		EsClient.printClusterStatus(client, indexConfig);
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
}
