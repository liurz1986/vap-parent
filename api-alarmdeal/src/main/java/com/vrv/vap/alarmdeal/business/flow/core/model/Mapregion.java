package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Mapregion entity
 */

@Data
@Table(name="mapregion")
@Entity
public class Mapregion implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1L;
	@Id
	@Column
	private String guid;     
	@Column(name="is_Main_Server")
	private Boolean mainServer;  //是否为主服务器
	@Column(name="name")
	private String name;   //级联名称
	@Column(name="ip")
	private String ip;  //级联服务器IP
	@Column(name="up_id")
	private String upId; //上级guid
	@Column(name="up_ip")
	private String upIp; //上级IP
	@Column(name="status")
	private Integer status; //级联状态
	@Column(name="run_status")
	private Integer runStatus; //级联运行状态
	@Column(name="code")
	private String code; //编码
	@Column(name="charge_man")
	private String chargeMan;   //负责人
	@Column(name="phone")
	private String phone; //电话
	@Column(name="lng")
	private BigDecimal lng;// 经度
	@Column(name="lat")
	private BigDecimal lat;// 纬度



}