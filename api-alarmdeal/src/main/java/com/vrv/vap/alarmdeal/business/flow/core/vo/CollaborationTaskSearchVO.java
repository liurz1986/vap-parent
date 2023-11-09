package com.vrv.vap.alarmdeal.business.flow.core.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月16日 上午11:26:52 
* 类说明 
*/
@Data
@ApiModel(value="协调任务查询VO")
public class CollaborationTaskSearchVO {
    
	@ApiModelProperty(value="开始时间")
	private String startTime;
	@ApiModelProperty(value="结束时间")
	private String endTime;
	@ApiModelProperty(value="排序字段")
	private String order_;   
	@ApiModelProperty(value="排序顺序")
	private String by_;   
	@ApiModelProperty(value="第几页")
	private Integer start_;//
	@ApiModelProperty(value="每页个数")
	private Integer count_;
	@ApiModelProperty(value="发起多个协同任务")
	private List<BusinessCollaboration> businessCollaborations;
}
