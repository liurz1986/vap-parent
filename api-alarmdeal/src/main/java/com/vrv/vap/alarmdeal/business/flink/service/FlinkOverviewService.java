package com.vrv.vap.alarmdeal.business.flink.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: 梁国露
 * @since: 2022/12/30 16:02
 * @description:
 */
public interface FlinkOverviewService {
    /**
     * 首页配置
     * @return
     */
    JSONObject config();

    /**
     * 首页概述
     * @return
     */
    JSONObject overview();

    /**
     * 首页任务列表概述
     * @return
     */
    JSONObject joboverview();
}
