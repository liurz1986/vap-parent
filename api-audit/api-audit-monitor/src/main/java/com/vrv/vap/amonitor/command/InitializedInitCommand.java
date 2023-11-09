package com.vrv.vap.amonitor.command;

import com.vrv.vap.amonitor.es.client.ElasticSearchManager;
import com.vrv.vap.amonitor.config.IndexConfig;
import com.vrv.vap.amonitor.es.init.IndexCache;
import com.vrv.vap.toolkit.config.PathConfig;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.tools.PathTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 初始化执行
 */
@Component
public class InitializedInitCommand implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(InitializedInitCommand.class);

    @Autowired
    PathConfig pathConfig;
    @Autowired
    ElasticSearchManager client;
    @Autowired
    IndexConfig indexConfig;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        // -----------------------初始化-----------------------
        PathTools.setPathConfig(pathConfig);
        Export.startWatcher();
        //		EsClient.printClusterStatus(client, indexConfig);
        IndexCache.setRedisTemplate(redisTemplate);
        IndexCache.cache(client, indexConfig);
    }
}
