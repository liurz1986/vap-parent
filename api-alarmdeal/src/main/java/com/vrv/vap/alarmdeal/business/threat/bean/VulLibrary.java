package com.vrv.vap.alarmdeal.business.threat.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月5日 上午9:59:41 
* 类说明  脆弱性库
*/
@Entity
@Data
@Table(name = "vul_library")
public class VulLibrary {

	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "vul_name")
	private String vulName;
	@Column(name = "vul_type")
	private String vulType;
	@Column(name = "id_obj")
	private String idObj;
	@Column(name = "effect_target")
	private String effectTarget;
	
}
