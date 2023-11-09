package com.vrv.vap.alarmdeal.business.flink.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl.AlarmEventDisposalServiceImpl;
import com.vrv.vap.alarmdeal.business.flink.constant.FlinkConstant;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkJobService;
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
public class FlinkJobServiceImpl implements FlinkJobService {
    private final Logger logger = LoggerFactory.getLogger(FlinkJobServiceImpl.class);
    /**
     * Job详情
     * @param id
     * @return
     */
    @Override
    public JSONObject info(String id) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id;
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！ info id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job详情异常信息
     * @param id
     * @return
     */
    @Override
    public JSONObject exceptions(String id) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/exceptions?maxExceptions=10";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！exceptions id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job详情检查点信息
     * @return
     */
    @Override
    public JSONObject checkpoints(String id) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/checkpoints";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！checkpoints id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job详情配置信息
     * @param id
     * @return
     */
    @Override
    public JSONObject config(String id) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/config";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！config id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job 退出
     * @param id
     * @return
     */
    @Override
    public JSONObject cancel(String id) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/yarn-cancel";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！cancel id ={},url = {}",id,url);
        }
        return null;
    }

    @Override
    public JSONObject vertices(String id, String name) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/vertices"+"/"+name;
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！vertices id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job 单任务详情
     * @param id
     * @param name
     * @return
     */
    @Override
    public JSONObject taskmanagers(String id, String name) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/vertices"+"/"+name+"/taskmanagers";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！taskmanagers id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job 单任务压力详情
     * @param id
     * @param name
     * @return
     */
    @Override
    public JSONObject backpressure(String id, String name) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/vertices"+"/"+name+"/backpressure";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！backpressure id ={},url = {}",id,url);
        }
        return null;
    }

    /**
     * Job 单任务指标配置
     * @param id
     * @param name
     * @return
     */
    @Override
    public JSONArray metrics(String id, String name, String paramMap) {
        String url = FlinkConstant.Config.url+"/jobs"+"/"+id+"/vertices"+"/"+name+"/metrics";
        if(StringUtils.isNotBlank(paramMap)){
            url += "?get="+paramMap;
        }
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONArray.parseArray(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！metrics id ={},url = {}",id,url);
        }
        return null;
    }
}
