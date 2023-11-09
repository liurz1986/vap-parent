package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
public interface CollectorRuleService extends BaseService<CollectorRule> {

    String buildConfigData(Map<String,Object> result,String renames);

    Map<String,Object> getFlumeRule(CollectorRule collectorRule,String step);

    String generateRules(List<CollectorRule> ruleList,Integer accessId,Boolean updateNew);

    void syncJsContent(Integer collectionId,String jsContent);
}
