package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年7月19日 上午10:22:23 
* 类说明    告警分数VO 
*/
@Data
public class AlarmScoreVO {
    
	private String ruleId;
	private String ruleName;
	private Float score; 
}
