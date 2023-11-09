package com.vrv.vap.alarmdeal.business.flink.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:01
 * @description:
 */
public interface FlinkJobManagerService {
    /**
     * Job配置
     * @return
     */
    JSONArray config();

    /**
     * Job指标
     * @param params
     * @return
     */
    JSONArray metrics(String params);

    /**
     * Job日志
     * @return
     */
    JSONObject log();

    /**
     * Job输出
     * @return
     */
    String stdout();

    /**
     * Job日志列表
     * @return
     */
    JSONObject logs();
}
