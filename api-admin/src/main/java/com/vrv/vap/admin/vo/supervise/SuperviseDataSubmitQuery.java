package com.vrv.vap.admin.vo.supervise;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel(value="数据上报查询")
public class SuperviseDataSubmitQuery extends Query {
	
    @ApiModelProperty("数据类型")
    private Integer dataType;
    @ApiModelProperty("上报状态")
    private Integer submitStatus;
    
	@ApiModelProperty("创建时间 开始时间")
	@Column(name="createTime")
	@QueryMoreThan
    @JsonProperty(value = "beginCreateTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date beginCreateTime;

    @ApiModelProperty("创建时间 结束时间")
	@Column(name="createTime")
	@QueryLessThan
    @JsonProperty(value = "endCreateTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endCreateTime;
    
    public Integer getDataType() {
		return dataType;
	}
	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}
	public Integer getSubmitStatus() {
		return submitStatus;
	}
	public void setSubmitStatus(Integer submitStatus) {
		this.submitStatus = submitStatus;
	}
	public Date getBeginCreateTime() {
		return beginCreateTime;
	}
	public void setBeginCreateTime(Date beginCreateTime) {
		this.beginCreateTime = beginCreateTime;
	}
	public Date getEndCreateTime() {
		return endCreateTime;
	}
	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

}
