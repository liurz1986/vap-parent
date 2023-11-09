package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class Alert {


	private Integer type;
	/**
	 * 系统编码，待定
	 */
	private String client_id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date update_time;


	private List<AbstractUpData> data;


}