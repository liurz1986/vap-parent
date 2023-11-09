package com.vrv.vap.alarmdeal.business.flink.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:02
 * @description:
 */
public interface FlinkTaskManagerService {

    /**
     * 任务配置
     * @return
     */
    JSONObject taskmanagers();

    /**
     * 任务配置详情
     * @param id
     * @return
     */
    JSONObject taskmanagersByid(String id);

    /**
     * 任务配置指标详情
     * @param id
     * @return
     */
    JSONArray taskmanagersBymetrics(String id, String params);

    /**
     * 任务配置日志
     * @param id
     * @return
     */
    JSONObject taskmanagersBylogs(String id);

    /**
     * 任务配置日志
     * @param id
     * @return
     */
    String taskmanagersBylog(String id);

    /**
     * 任务配置线程转储详情
     * @param id
     * @return
     */
    JSONObject taskmanagersBythreaddump(String id);

    /**
     * 任务页面根据名称查询日志
     * @param id
     * @param name
     * @return
     */
    String taskmanagersGetlogsByName(String id ,String name);
}
