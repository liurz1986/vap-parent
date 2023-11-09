package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import java.util.List;

/**
 * @Author: 梁国露
 * @Date: 2022年08月02日 13:48:46
 * @Description:
 */
public interface RuleFlinkTypeService {
    /**
     * 同步规则启动类型
     *
     * @param type
     */
    void putRuleFlinkStart(String type);

    /**
     * 判断现有规则启动类型是否与设定一致
     * @return
     */
    boolean checkNowRuleStartType(List<String> jobNames);

    /**
     * 获取规则启动方式
     * @return
     */
    String getRuleFlinkStart();

    /**
     * 设置规则启动num个为一组
     * @param num
     */
    void putRuleFlinkStartNum(int num);

    /**
     * 获取规则启动分组数
     * @return
     */
    int getRuleFlinkStartNum();

    /**
     * 初始化任务
     */
    void initRiskEventRule();
}
