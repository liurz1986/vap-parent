package com.vrv.vap.admin.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@ApiModel(value="操作信息")
@Data
public class DbOperationInfoVO extends Query {
	@ApiModelProperty("操作状态")
	private Integer operationStatus;

	@ApiModelProperty("操作类型")
	private Integer operationType;

	@ApiModelProperty("数据类型")
	@QueryLike
	private String dataTypes;

	@ApiModelProperty("存储介质")
	private String fileStorage;

	@ApiModelProperty("开始时间")
	@QueryMoreThan
	@JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="start_time")
	private Date startTime;

	@ApiModelProperty("结束时间")
	@QueryLessThan
	@JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@Column(name="start_time")
	private Date endTime;
}
