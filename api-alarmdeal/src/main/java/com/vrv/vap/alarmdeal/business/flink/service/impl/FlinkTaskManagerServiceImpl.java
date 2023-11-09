package com.vrv.vap.alarmdeal.business.flink.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.constant.FlinkConstant;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkTaskManagerService;
import com.vrv.vap.alarmdeal.frameworks.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:04
 * @description:
 */
@Service
public class FlinkTaskManagerServiceImpl implements FlinkTaskManagerService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(FlinkTaskManagerServiceImpl.class);

    @Override
    public JSONObject taskmanagers() {
        String url = FlinkConstant.Config.url+"/taskmanagers";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagers url = {}",url);
        }
        return null;
    }

    @Override
    public JSONObject taskmanagersByid(String id) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id;
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagersByid id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public JSONArray taskmanagersBymetrics(String id, String params) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id+"/metrics";
        if(StringUtils.isNotBlank(params)){
            url+="?get="+params;
        }
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONArray.parseArray(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！ taskmanagersBymetrics id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public JSONObject taskmanagersBylogs(String id) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id+"/logs";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagersBylogs id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public String taskmanagersBylog(String id) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id+"/log";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  res;
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagersBylog id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public JSONObject taskmanagersBythreaddump(String id) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id+"/thread-dump";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public String taskmanagersGetlogsByName(String id, String name) {
        String url = FlinkConstant.Config.url+"/taskmanagers"+"/"+id+"/logs/"+name;
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  res;
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagersGetlogsByName id ={},url = {}",id,url);
        }
        return null;
    }
}
