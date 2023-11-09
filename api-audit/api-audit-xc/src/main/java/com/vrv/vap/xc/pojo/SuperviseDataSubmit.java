package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("supervise_data_submit")
public class SuperviseDataSubmit {
	private String guid;

	@ApiModelProperty("数据类型")
	private Integer dataType;

	@ApiModelProperty("生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty("上报状态")
	private Integer submitStatus;

	@ApiModelProperty("上报时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	@ApiModelProperty("数据内容")
	private String data;

	private String onlineSubmitResult;
}