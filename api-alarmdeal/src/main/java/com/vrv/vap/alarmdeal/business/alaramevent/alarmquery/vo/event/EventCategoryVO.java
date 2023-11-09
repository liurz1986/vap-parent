package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 事件分类VO
 * @author wd-pc
 *
 */
@Data
@ApiModel(value="事件分类对象")
public class EventCategoryVO {
	@ApiModelProperty(value="告警入参对象主键ID")
	private String id;
	@ApiModelProperty(value="告警标题")
	private String title;
	@ApiModelProperty(value="告警等级")
	private Integer priorityLevel;
	@ApiModelProperty(value="事件描述")
	private String eventDesc;
	// 事件标识
	@ApiModelProperty(value="事件标识")
	private String code;
	// 事件标识层级关系
	@ApiModelProperty(value="事件标识层级关系")
	private String codeLevel;
	@ApiModelProperty(value="创建时间")
	private String createdTime;
	@ApiModelProperty(value="修改时间")
	private String modifiedTime;
	@ApiModelProperty(value="父级节点Id")
	private String parentId;
	/**
	 * 0其他1系统内置
	 */
	@ApiModelProperty(value="告警事件状态，0其他1系统内置")
	private Integer status;
	/**
	 * 排序字段
	 */
	@ApiModelProperty(value="排序字段")
	private Integer orderNum;
	@ApiModelProperty(value="攻击类型")
	private String attackFlag;
	@ApiModelProperty(value="事件表排序字段") 
	private String order_;    // 排序字段
	@ApiModelProperty(value="事件表排序顺序") 
	private String by_;   // 排序顺序
	@ApiModelProperty(value="事件表起始行") 
	private Integer start_;//
	@ApiModelProperty(value="每页个数") 
	private Integer count_;
	
	@ApiModelProperty(value="威胁来源") 
	private String threatSource; //威胁来源
	@ApiModelProperty(value="威胁描述") 
	private String threatDesc; //威胁来源
	
	@ApiModelProperty(value="威胁分类") 
	private String threatClassification; //威胁分类
	
	@ApiModelProperty(value="动机描述") 
	private String motivateDesc; //动机描述
	
	@ApiModelProperty(value="动机赋值") 
	private Integer motivateAssignment; //动机赋值
	
	@ApiModelProperty(value="能力描述") 
	private String abilityDesc; //能力描述
	
	
	@ApiModelProperty(value="能力赋值") 
	private Integer abilityAssignment; //能力赋值
	
	@ApiModelProperty(value="作用目标") 
	private String effectTarget; //作用目标
	
	@ApiModelProperty(value="关联漏洞") 
	private String relateVulnerability; //关联漏洞



	@ApiModelProperty(value="威胁摘要")
	private  String threadSummary;  //威胁摘要

	@ApiModelProperty(value="危害")
	private String harm;	//危害

	@ApiModelProperty(value="原理")
	private  String principle;	//原理

	@ApiModelProperty(value="处置建议")
	private  String dealAdvice;	//处置建议

	@ApiModelProperty(value = "定义类型")
	private  String type;


	
}
