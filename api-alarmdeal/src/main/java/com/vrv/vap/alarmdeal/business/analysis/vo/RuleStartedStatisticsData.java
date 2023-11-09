package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

@Data
public class RuleStartedStatisticsData{
	String categoryId;
	String categoryName;
	String categoryHierarchy;
	String codeLevel;//等同于  eventCode
	Integer openRuleCount;
	Integer ruleTotal;
}