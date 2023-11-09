package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DataSubmitInfo {

	@ApiModelProperty("系统编码")
	private String client_id;

	@ApiModelProperty("任务编号")
	private String notice_id;

	@ApiModelProperty("任务状态")
	private Integer ssa_run_state;

	@ApiModelProperty("协办附件")
	private Object co_file;

	@ApiModelProperty("预警附件")
	private Object warn_file;

	@ApiModelProperty("数据类型")
	private Integer type;

	@ApiModelProperty("生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

	@ApiModelProperty("数据内容")
	private Object data;
}