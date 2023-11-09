package com.vrv.rule.model;

import java.io.Serializable;

import lombok.Data;
/**
 * 告警规则
 * @author wd-pc
 *
 */

@Data
public class RiskEventRule implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String riskEventId;   //事件分类ID
	private String name;  //规则名称
	private Integer type;  //类型
	private String desc;  //规则描述
	private String note; //规则备注
	private String extend1;
	private String extend2;
	private Boolean started;  //是否启动
	private String createdTime;  //创建时间
	private String modifiedTime; //修改时间
	private String assetip;   //资产ip
	private String assetguid; //资产guid
	private String warmType;  //告警类型
	private String cascadeState;  //级联状态
	private String levelstatus;  //等级状态
	private String validations;  //告警有效性
	private String ruleCode;    //规则编码
	private String field_info;  //处置字段
	private String data_source;  //数据源
	private String rule_complex; //规则类型
    private Boolean attack_event; //攻击事件
	private String main_class; //flink启动主类
	private String job_name; //任务名称
	private String rule_desc; //规则描述
	private String rule_field_json; //规则表单描述
	private Float unitScore; //单位分数
	private Float maxScore; //最高分数
	private String riskSql;  //对应的sql规则
	private String tableName; //表名
	private String logPath; //日志路径
	private String flag; //日志类型标识
	private String tableLabel; //日志类型标识
	private String knowledgeTag; //知识库标签
	private String initStatus; //告警初始化
}
