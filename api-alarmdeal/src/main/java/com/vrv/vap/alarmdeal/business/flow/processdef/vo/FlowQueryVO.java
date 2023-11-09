package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月18日 下午5:43:51 
* 类说明     流程查询筛选条件
*/
@Data
@ApiModel(value="流程查询VO")
public class FlowQueryVO {
	@ApiModelProperty(value="流程名称")
	private String processName;

}
