package com.vrv.vap.monitor.server.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="告警组信息")
public class AlarmItemGroupVO extends Query {
	@ApiModelProperty("更新状态 0 未处理，1 已处理，2 忽略")
	private Integer updateStatus;
	@ApiModelProperty("告警数据id,逗号拼接")
	private String ids;

	public Integer getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(Integer updateStatus) {
		this.updateStatus = updateStatus;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
