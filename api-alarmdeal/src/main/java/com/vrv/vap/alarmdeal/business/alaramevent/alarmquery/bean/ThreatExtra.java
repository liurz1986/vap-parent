package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月15日 下午3:40:16 
* 类说明 
*/
@Entity
@Data
@Table(name = "threat_extra")
public class ThreatExtra implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="threat_library_id")
	private String threat_library_id;
	@ExcelField(title = "详细信息", order = 1)
	@Column(name="detail_info")
	private String detail_info;
	@ExcelField(title = "风险危害", order = 2)
	@Column(name="threat_harm")
	private String threat_harm;
	@ExcelField(title = "处理意见", order = 3)
	@Column(name="deal_advice")
	private String deal_advice;
	@Column(name="safe_advice")
	@ExcelField(title = "处理意见", order = 4)
	private String safe_advice;
}
