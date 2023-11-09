package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit;

import java.util.List;
import java.util.Map;

/**
 * @Author: 梁国露
 * @Date: 2022年08月10日 14:00:53
 * @Description: 规则启动适配
 */
public interface RuleStartFit {
    /**
     * 初始启动
     * @param jobList
     */
    public void startRule(List<String> jobList);

    /**
     * 启动任务
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param map
     */
    public void startTask(List<String> guidList, String riskEventId, String startType,Map<String,List<String>> map);

    /**
     * 停止任务
     * @param guidList
     * @param riskEventId
     * @param startType
     * @param map
     */
    public void stopTask(List<String> guidList, String riskEventId, String startType,Map<String,List<String>> map);
}
