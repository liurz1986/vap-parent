package com.vrv.vap.alarmdeal.business.analysis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 下午5:40:55 
* 类说明 
*/
@Entity
@Data
@Table(name = "threat_info")
public class ThreatInfo {

	@Id
	@Column(name="id")
	private String id;  //主键id
	@Column(name="library_guid")
	private String library_guid; //库id
	@OneToOne
	@JoinColumn(name ="library_guid",referencedColumnName="id",insertable=false,updatable=false)
	private EventCategory eventCategory;
	@Column(name="threat_frequence")
	private Integer threat_frequence; //威胁频率
	private Integer threat_value; //威胁赋值
	@Column(name="threat_desc")
	private String threat_desc; //评估描述
	 
}
