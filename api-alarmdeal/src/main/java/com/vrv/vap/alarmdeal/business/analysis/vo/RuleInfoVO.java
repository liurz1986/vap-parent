package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年9月27日 下午3:28:29 
* 类说明  规则属性填充
*/
@Data
public class RuleInfoVO {

	private String riskEventName; //风险名称
	private String ruleName; //规则名称
	private String ruleId; //规则ID
    private String codeLevel;
    private String weight; //告警等级
    private Boolean isStart;
    private String riskEventId; //事件分类Id
    private String tableLabel; //表名
    private String attackFlag; //是否为攻击告警的标识
    private String initStatus; //告警初始化状态
    private String tag; //告警标签
    private String muitlVersionStr; //综合版本Str
    private Integer  failedStatus;  //失陷状态，
    private String dealAdvice;  //处理意见
    private String harm; //危害
    private String threatSource; //威胁来源
    private String attackLine;  //攻击阶段
    private String threatCredibility; //威胁可信度
    private String principle;	//原理
    private String dataSource;  //威胁数据来源
    private Boolean produceThreat;  //产生威胁
    private String extend2;  //响应内容

}
