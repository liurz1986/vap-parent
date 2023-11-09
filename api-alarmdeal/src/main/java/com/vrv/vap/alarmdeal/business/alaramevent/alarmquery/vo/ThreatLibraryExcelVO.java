package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import com.vrv.vap.exportAndImport.excel.annotation.ExcelField;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月15日 下午4:34:30 
* 类说明 
*/
@Data
public class ThreatLibraryExcelVO {
   
	@ExcelField(title = "威胁编号", order = 1)
	private String id;  //主键id
	@ExcelField(title = "威胁名称", order = 2)
	private String title; //威胁名称
	@ExcelField(title = "威胁来源", order = 3)
	private String threatSource; //威胁来源
	@ExcelField(title = "威胁分类", order = 4)
	private String threatClassification; //威胁分类
	@ExcelField(title = "威胁描述", order = 5)
	private String eventDesc; //威胁描述
	@ExcelField(title = "动机描述", order = 6)
	private String motivateDesc; //动机描述
	@ExcelField(title = "动机赋值", order = 7)
	private Integer motivateAssignment; //动机赋值
	@ExcelField(title = "能力描述", order = 8)
	private String abilityDesc; //能力描述
	@ExcelField(title = "能力赋值", order = 9)
	private Integer abilityAssignment; //能力赋值
	@ExcelField(title = "作用目标", order = 10)
	private String effectTarget; //作用目标
	@ExcelField(title = "关联漏洞", order = 11)
	private String relateVulnerability; //关联漏洞
	@ExcelField(title = "详细信息", order = 12)
	private String detail_info;
	@ExcelField(title = "风险危害", order = 13)
	private String threat_harm;
	@ExcelField(title = "处理意见", order = 14)
	private String deal_advice;
	@ExcelField(title = "安全知识库", order = 15)
	private String safe_advice;
	
	
}
