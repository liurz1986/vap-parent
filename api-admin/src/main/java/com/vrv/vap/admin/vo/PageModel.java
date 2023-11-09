package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel("分页模型")
public class PageModel {

	@ApiModelProperty("开始条数")
	@JsonProperty(value = "start_", access = Access.WRITE_ONLY)
	private int myStart = 0;

	@ApiModelProperty("返回数量")
	@JsonProperty(value = "count_", access = Access.WRITE_ONLY)
	private int myCount = 10;

	@ApiModelProperty("排序字段")
	@JsonProperty(value = "order_", access = Access.WRITE_ONLY)
	private String order;

	@ApiModelProperty("排序:desc/asc")
	@JsonProperty(value = "by_", access = Access.WRITE_ONLY)
	private String by;

	@ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
	@JsonProperty(value = "start_time", access = Access.WRITE_ONLY)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date myStartTime;

	@ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
	@JsonProperty(value = "end_time", access = Access.WRITE_ONLY)
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date myEndTime;

	public PageModel() {
	}

	public PageModel(Integer myStart, Integer myCount) {
		this(myStart, myCount, null, null);
	}

	public PageModel(Integer myStart, Integer myCount, String order, String by) {
		this.myStart = myStart;
		this.myCount = myCount;
		this.order = order;
		this.by = by;
	}

	public int getMyStart() {
		return myStart;
	}

	public void setMyStart(int myStart) {
		this.myStart = myStart;
	}

	public int getMyCount() {
		return myCount;
	}

	public void setMyCount(int myCount) {
		this.myCount = myCount;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public Date getMyStartTime() {
		return myStartTime;
	}

	public void setMyStartTime(Date myStartTime) {
		this.myStartTime = myStartTime;
	}

	public Date getMyEndTime() {
		return myEndTime;
	}

	public void setMyEndTime(Date myEndTime) {
		this.myEndTime = myEndTime;
	}
}
