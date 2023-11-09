package com.vrv.vap.line.client;

import org.apache.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

/**
 * 获取es客户端
 *
 * @author xw
 * @date 2015年10月20日
 */
@Component
public class ElasticSearchManager {

    private static final Logger log = Logger.getLogger(ElasticSearchManager.class);

    private ElasticSearchManager() {

    }

    public static RestHighLevelClient getClient() {
        return EsClient.getInstance();
    }

}
