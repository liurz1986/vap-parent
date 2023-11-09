package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 资产窃泄密值
 */
@Data
@Table(name="app_steal_leak_value")
@Entity
@ApiModel(value = "应用系统窃泄密值")
public class AppStealLeakValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name="app_id")
	private Integer appId;
	@Column(name="steal_leak_value")
	private Integer stealLeakValue;
	@Column(name="create_time")
	private Date createTime;
	@Column(name="type")
	private Integer type;

}
