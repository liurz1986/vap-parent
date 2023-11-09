package com.vrv.vap.admin.common.manager;

import org.slf4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 获取es客户端
 *
 * @author xw
 * @date 2015年10月20日
 */
@Component
public class ElasticSearchManager {

	private static final Logger log = LoggerFactory.getLogger(ElasticSearchManager.class);

	private ElasticSearchManager() {

	}

	public static RestHighLevelClient getClient() {
		log.info("--------------SearchManager getClient start---------------");
		return ESClient.getInstance();
	}
}
