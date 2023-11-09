package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@ApiModel("级联数据接收对象")
@Table(name = "supervise_data_receive")
public class SuperviseDataReceive {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ApiModelProperty("数据类型")
	@Column(name = "data_type")
	private Integer dataType;

	@ApiModelProperty("生成时间")
	@Column(name = "create_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@ApiModelProperty("接收类型")
	@Column(name = "receive_type")
	private Integer receiveType;

	/*@ApiModelProperty("接收状态 0 失败 1 成功")
	@Column(name = "receive_status")
	private Integer receiveStatus;*/

	@ApiModelProperty("接收时间")
	@Column(name = "receive_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date receiveTime;

	@ApiModelProperty("数据内容")
	@Column(name = "data")
	private String data;
}