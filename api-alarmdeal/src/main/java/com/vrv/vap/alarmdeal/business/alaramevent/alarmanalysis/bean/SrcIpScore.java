package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月15日 下午3:40:16 
* 类说明 
*/
@Entity
@Data
@Table(name = "src_ip_score")
public class SrcIpScore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="guid")
	private String guid;
	@Column(name="srcIp")
	private String srcIp;
	@Column(name="create_date")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_date;
	@Column(name="score_value")
	private Float score_value;
}
