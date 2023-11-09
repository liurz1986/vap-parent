package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import lombok.Data;

/**
 * 上报抽象父类
 */
@Data
public class UpAbstractReportVO {
	private Integer type;
	//系统编码（用户注册时候返回的信息）
	private String client_id;
	private String update_time;
	private String notice_id;
}
