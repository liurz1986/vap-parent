package com.vrv.vap.alarmdeal.business.flow.core.vo;
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年8月20日 下午2:37:40 
* 类说明  流程实例VO
*/

import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTask;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskLog;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
@Data
@ApiModel(value="工单查询VO")
public class BusinessTicketVO {
	@ApiModelProperty(value="工单名称") 
	private String name;
	@ApiModelProperty(value="用户userId") 
	private String userId;
	@ApiModelProperty(value="流程实例")
	private BusinessIntance businessIntance; //流程实例
	@ApiModelProperty(value="流程任务")
	private BusinessTask businessTask; //流程任务
	@ApiModelProperty(value="流程记录")
	private BusinessTaskLog businessTaskLog; //流程记录
	@ApiModelProperty(value="我的工单")
	private MyTicket myTicket; //我的工单
	@ApiModelProperty(value="流程状态")
	private String ticketStatus;
	@ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
	@ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
	@ApiModelProperty(value="排序字段")
	private Integer start_ = 0;
	@ApiModelProperty(value="每个个数")
	private Integer count_ = 20;
}
