package com.vrv.vap.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;

@ApiModel(value = "DeleteQuery", description = "通用批量删除，接收一个 ids 参数")
public class DeleteQuery {

	@ApiParam(required = true)
	@ApiModelProperty(value="需要删除的 id 列表，多条以逗号 ',' 分割")
	private String ids;

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
