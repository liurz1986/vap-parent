package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.model.CollectorRuleCollection;

import java.util.List;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
public class CollectorRuleCollectionVO extends CollectorRuleCollection {

    private Integer ruleCount;

    private List<CollectorRule> ruleList;

    public Integer getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(Integer ruleCount) {
        this.ruleCount = ruleCount;
    }

    public List<CollectorRule> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<CollectorRule> ruleList) {
        this.ruleList = ruleList;
    }
}
