package com.vrv.vap.admin.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Table(name = "supervise_data_submit")
public class SuperviseDataSubmit {
	@Id
	private String guid;

	@ApiModelProperty("数据类型")
	@Column(name = "data_type")
	private Integer dataType;

	@ApiModelProperty("生成时间")
	@Column(name = "create_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty("上报状态")
	@Column(name = "submit_status")
	private Integer submitStatus;

	@ApiModelProperty("上报时间")
	@Column(name = "submit_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	@ApiModelProperty("数据内容")
	@Column(name = "data")
	private String data;

	@Column(name = "online_submit_result")
	private String onlineSubmitResult;
}