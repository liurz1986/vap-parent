package com.vrv.vap.alarmdeal.business.analysis.model;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.OperationLog;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public  class AuthorizationControl{
	
	@ApiModelProperty(value = "可以操作的用户")
	public List<GuidNameVO>  canOperateUser;
	
	@ApiModelProperty(value = "可以操作的角色")
	public List<GuidNameVO>  canOperateRole;
	
	@ApiModelProperty(value = "操作人记录")
	public List<OperationLog> operatorRecord;
}
