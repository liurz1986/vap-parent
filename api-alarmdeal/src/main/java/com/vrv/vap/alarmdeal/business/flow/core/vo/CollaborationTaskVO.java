package com.vrv.vap.alarmdeal.business.flow.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 下午2:27:23 
* 类说明 
*/
@Data
@ApiModel(value="协调任务VO")
public class CollaborationTaskVO {
	@ApiModelProperty(value="工单ID(发起协同的工单)")
	private String ticketId; //工单ID(发起协同的工单)
	@ApiModelProperty(value="级联的信息")
    private String mapRegionContent; //级联的信息
	@ApiModelProperty(value="工单信息")
	private String ticketContent; //工单信息
	@ApiModelProperty(value="协同任务创建的工单")
	private String collabrationTicketId; //协同任务创建的工单
	@ApiModelProperty(value="协同任务的ID")
	private String collabrationId; //协同任务的ID
	@ApiModelProperty(value="协同任务更新状态")
	private String status; //协同任务更新状态
	@ApiModelProperty(value="上级本级IP")
	private String upIp; //工单ID(发起协同的工单)
	
}
