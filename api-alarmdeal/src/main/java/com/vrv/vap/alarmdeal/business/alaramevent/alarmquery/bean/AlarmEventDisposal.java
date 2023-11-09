package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.util.Date;
import java.util.List;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import com.vrv.vap.es.model.PrimaryKey;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 告警事件处置（一个事件可以多个处置记录）
 * @author sj100d
 *
 */
@Data
@PrimaryKey(value="guid")
public class AlarmEventDisposal {

	@EsField("guid")
	@ApiModelProperty(value = "guid")
	String guid;
	

	
	@EsField("event_type")
	@ApiModelProperty(value = "事件类型")
	Integer eventType;
	
	
//	——	event_id
	@EsField("event_id")
	@ApiModelProperty(value = "事件id")
	String  eventId;

	
//	处置人员姓名	disposal_person_name
	@EsField("disposal_person_name")
	@ApiModelProperty(value = "处置人员姓名")
	String disposalPersonName;
//	处置人角色	disposal_person_role
	@EsField("disposal_person_role")
	@ApiModelProperty(value = "处置人角色")
	String disposalPersonRole;
	
//	处置时间	disposal_time
	@EsField("disposal_time")
	@ApiModelProperty(value = "处置时间")
	Date disposalTime;
	
	
//	是否误报	is_misreport
	@EsField("is_misreport")
	@ApiModelProperty(value = "是否误报")
	Boolean isMisreport;
	
//	处置状态	disposal_status
	@EsField("disposal_status")
	@ApiModelProperty(value = "处置状态")
	Integer disposalStatus;
	
	
//	人员数量	person_count
	@EsField("person_count")
	@ApiModelProperty(value = "人员数量")
	Integer personCount;
	
	List<ResponsiblePerson> responsiblePersons;

 
	@EsField("device_count")
	@ApiModelProperty(value = "设备数量")
	Integer deviceCount;
	
	List<DisposalDevice> disposalDevices;
	
//	文件数量	秘密文件数量	file_mm01_count
	@EsField("file_mm01_count")
	@ApiModelProperty(value = "秘密文件数量")
	Integer fileMm01Count;
	
//	机密文件数量	file_mm02_count 
	@EsField("file_mm02_count")
	@ApiModelProperty(value = "机密文件数量")
	Integer fileMm02Count;

//	绝密文件数量	file_mm03_count 
	@EsField("file_mm03_count")
	@ApiModelProperty(value = "绝密文件数量")
	Integer fileMm03Count;

//成因分析	——	cause 
	@EsField("cause")
	@ApiModelProperty(value = "成因分析")
	String cause;

//结果评估	——	result_evaluation 
	@EsField("result_evaluation")
	@ApiModelProperty(value = "结果评估")
	String resultEvaluation;

//事件详情	——	event_details 
	@EsField("event_details")
	@ApiModelProperty(value = "事件详情")
	String eventDetails;

//技术整改措施	——	rectification 
	@EsField("rectification")
	@ApiModelProperty(value = "技术整改措施")
	String rectification;

//附件	——	attachment 
	@EsField("attachment")
	@ApiModelProperty(value = "附件")
	String attachment;

}
