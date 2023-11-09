package com.vrv.vap.alarmdeal.business.threat.bean;

import lombok.Data;

import javax.persistence.*;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 下午5:40:55 
* 类说明 
*/
@Entity
@Data
@Table(name = "vul_manage")
public class VulManage {

	@Id
	@Column(name="id")
	private String id;  //主键id
	@Column(name="vul_id")
	private String vulId; //库id
	@OneToOne
	@JoinColumn(name ="vul_id",referencedColumnName="id",insertable=false,updatable=false)
	private VulLibrary  vulLibrary;
	@Column(name="vul_value")
	private Integer vulValue; //严重程度
	 
}
