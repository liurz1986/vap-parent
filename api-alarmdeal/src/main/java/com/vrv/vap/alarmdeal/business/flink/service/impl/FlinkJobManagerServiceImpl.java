package com.vrv.vap.alarmdeal.business.flink.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.constant.FlinkConstant;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkJobManagerService;
import com.vrv.vap.alarmdeal.frameworks.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:02
 * @description:
 */
@Service
public class FlinkJobManagerServiceImpl implements FlinkJobManagerService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(FlinkJobManagerServiceImpl.class);

    /**
     * Job配置
     * @return
     */
    @Override
    public JSONArray config() {
        String url = FlinkConstant.Config.url+"/jobmanager"+"/config";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONArray.parseArray(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！config url = {}",url);
        }
        return null;
    }

    /**
     * Job指标
     * @param params
     * @return
     */
    @Override
    public JSONArray metrics(String params) {
        String url = FlinkConstant.Config.url+"/jobmanager"+"/metrics";
        if(StringUtils.isNotBlank(params)){
            url+="?get="+params;
        }
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONArray.parseArray(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！metrics url = {}",url);
        }
        return null;
    }

    /**
     * Job日志
     * @return
     */
    @Override
    public JSONObject log() {
        String url = FlinkConstant.Config.url+"/jobmanager/log";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！log url = {}",url);
        }
        return null;
    }

    /**
     * Job输出
     * @return
     */
    @Override
    public String stdout() {
        String url = FlinkConstant.Config.url+"/jobmanager/stdout";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  res;
        }catch (Exception exception){
            logger.error("flink 接口调用失败！stdout url = {}",url);
        }
        return null;
    }

    /**
     * Job日志列表
     * @return
     */
    @Override
    public JSONObject logs() {
        String url = FlinkConstant.Config.url+"/jobmanager/logs";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！logs url = {}",url);
        }
        return null;
    }
}
