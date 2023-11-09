package com.vrv.vap.alarmdeal.business.flow.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 下午3:10:04 
* 类说明   工单发起协同任务的VO
*/
@Data
@ApiModel(value=" 工单发起协同任务的VO")
public class BusinessCollaboration {
	@ApiModelProperty(value="当前登录人ID")
    private String userId; //当前登录人名称
	@ApiModelProperty(value="工单ID")
    private String ticketId; //工单ID
	@ApiModelProperty(value="级联区域ID")
    private String mapRegionId; //级联区域ID
}
