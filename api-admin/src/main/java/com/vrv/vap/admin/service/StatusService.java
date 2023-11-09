package com.vrv.vap.admin.service;

import java.util.Map;

/**
 * @author lilang
 * @date 2020/10/28
 * @description
 */
public interface StatusService {

    /**
     * 获取ES集群信息
     * @return
     */
    Map<String,Object> getEsClusterInfo();

    /**
     * 获取logstash信息
     * @return
     */
    Map<String,Object> getLogStashInfo();

    Map<String,Object> extractKafkaData();

    String getPushUrl();
}
