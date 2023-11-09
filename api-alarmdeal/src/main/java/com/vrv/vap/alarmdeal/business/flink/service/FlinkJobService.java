package com.vrv.vap.alarmdeal.business.flink.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:01
 * @description:
 */
public interface FlinkJobService {
    /**
     * Job详情
     * @param id
     * @return
     */
    JSONObject info(String id);

    /**
     * Job详情异常信息
     * @param id
     * @return
     */
    JSONObject exceptions(String id);

    /**
     * Job详情检查点信息
     * @return
     */
    JSONObject checkpoints(String id);

    /**
     * Job详情配置信息
     * @param id
     * @return
     */
    JSONObject config(String id);

    /**
     * Job 退出
     * @param id
     * @return
     */
    JSONObject cancel(String id);

    /**
     * Job 单任务详情
     * @param id
     * @param name
     * @return
     */
    JSONObject vertices(String id,String name);

    /**
     * Job 单任务详情
     * @param id
     * @param name
     * @return
     */
    JSONObject taskmanagers(String id,String name);

    /**
     * Job 单任务压力详情
     * @param id
     * @param name
     * @return
     */
    JSONObject backpressure(String id,String name);

    /**
     * Job 单任务指标配置
     * @param id
     * @param name
     * @return
     */
    JSONArray metrics(String id, String name, String param);
}
