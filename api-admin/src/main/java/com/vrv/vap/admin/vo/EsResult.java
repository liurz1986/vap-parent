package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * es查询返回
 * 
 * @author xw
 *
 * @date 2018年5月3日
 */
@ApiModel("es 查询返回结果实体类")
public class EsResult extends VList {

	/**
	 * 实际查询数据总量
	 */
	@ApiModelProperty("实际查询数据总量")
	private long totalAcc;

	public long getTotalAcc() {
		return totalAcc;
	}

	public void setTotalAcc(long totalAcc) {
		this.totalAcc = totalAcc;
	}

}
