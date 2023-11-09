package com.vrv.vap.alarmdeal.business.flink.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.flink.constant.FlinkConstant;
import com.vrv.vap.alarmdeal.business.flink.service.FlinkOverviewService;
import com.vrv.vap.alarmdeal.frameworks.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:03
 * @description:
 */
@Service
public class FlinkOverviewServiceImpl implements FlinkOverviewService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(FlinkOverviewServiceImpl.class);
    /**
     * 首页配置
     * @return
     */
    @Override
    public JSONObject config() {
        String url = FlinkConstant.Config.url+"/config";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！config url = {}",url);
        }
        return null;
    }

    /**
     * 首页概述
     * @return
     */
    @Override
    public JSONObject overview() {
        String url = FlinkConstant.Config.url+"/overview";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！overview url = {}",url);
        }
        return null;
    }

    /**
     * 首页任务列表概述
     * @return
     */
    @Override
    public JSONObject joboverview() {
        String url = FlinkConstant.Config.url+"/jobs/overview";
        try {
            String res = HttpClientUtils.sendGetRequest(url);
            return  JSONObject.parseObject(res);
        }catch (Exception exception){
            logger.error("flink 接口调用失败！joboverview url = {}",url);
        }
        return null;
    }
}
