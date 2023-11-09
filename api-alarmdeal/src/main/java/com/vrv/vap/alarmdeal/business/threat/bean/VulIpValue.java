package com.vrv.vap.alarmdeal.business.threat.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ip脆弱性值
 */
@Table(name = "vul_ip_value")
@Entity
@Data
public class VulIpValue {
	@Id
	private String guid;
	private String ip;
	private Integer value;
	private String time;
}