package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.RuleTypeConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.RuleStartFit;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.rulestartfit.RuleStartInterFace;
import com.vrv.vap.jpa.spring.SpringUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: 梁国露
 * @Date: 2022年08月10日 14:17:13
 * @Description: 适配类
 */
public class RuleStartAdapter implements RuleStartInterFace {
    RuleStartFit ruleStartFit;

    /**
     *
     * @param audioType
     */
    public RuleStartAdapter(String audioType){
        if(RuleTypeConstant.DATASOURCE.equalsIgnoreCase(audioType)){
            ruleStartFit = SpringUtil.getBean("ruleStartFitByDb",RuleStartFitByDb.class);
        }else if (RuleTypeConstant.CATEGORY.equalsIgnoreCase(audioType)){
            ruleStartFit = SpringUtil.getBean("ruleStartFitByCategory",RuleStartFitByCategory.class);
        }
    }

    @Override
    public void startRule(List<String> jobList) {
        ruleStartFit.startRule(jobList);
    }

    @Override
    public void startTask(List<String> guidList, String riskEventId, String startType,Map<String,List<String>> map) {
        ruleStartFit.startTask(guidList,riskEventId,startType,map);
    }

    @Override
    public void stopTask(List<String> guidList, String riskEventId, String startType,Map<String,List<String>> map) {
        ruleStartFit.stopTask(guidList,riskEventId,startType,map);
    }
}
