package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.List;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年9月26日 下午4:43:59 
* 类说明    告警合并筛选字段 
*/
@Data
public class FieldInfoVO {
     
	private Boolean isStart; //规则是否开启
	private List<String> field; //筛选字段
	private long timeSpan; //时间窗口时间
	private String rulePolicy ; //告警状态筛选
	
}
