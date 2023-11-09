package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年9月29日 下午5:41:47 
* 类说明  转专家相关信息
*/
@Data
@ApiModel(value="告警转专家/转处置接口")
public class ExpertVO{

 
	@ApiModelProperty(value="告警主键ID")
	private String analysisId; //告警guid
	@ApiModelProperty(value="当前登陆用户")
	private String userId;
	@ApiModelProperty(value="当前登陆用户名称")
	private String userName;
	@ApiModelProperty(value="工单编号")
	private String code;
	@ApiModelProperty(value="专家userId/处理人userId")
	private String users;
	@ApiModelProperty(value="专家用户名/处理人姓名")
	private String expertNames;
	@ApiModelProperty(value="截止日期")
	private String deadline;
	@ApiModelProperty(value="工单名称")
	private String ticketName;
	@ApiModelProperty(value="工单描述")
	private String ticketDesc;
	@ApiModelProperty(value="重要程度")
	private String ticketInflunce;
	@ApiModelProperty(value="上传文件guid")
	private String fileguids;
	@ApiModelProperty(value="研判角色")
	private String judgePerson;
	@ApiModelProperty(value="研判角色名称")
	private String judgeRoleName;
	@ApiModelProperty(value="监管角色")
	private String monitorPerson;
	@ApiModelProperty(value="处理角色")
	private String dealPerson;
	@ApiModelProperty(value="处理角色名称")
	private String dealRoleName;
	@ApiModelProperty(value="关联区域")
	private String orgName;
	@ApiModelProperty(value="上报标识")
	private String reportFlag;
	@ApiModelProperty(value="研判角色类型")
	private String judgetype;
	@ApiModelProperty(value="是否失陷")
	private String isFall;
	@ApiModelProperty(value="是否研判")
	private String isJudge;

}
