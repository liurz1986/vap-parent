package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class  ResponsiblePerson{
	
	
//	责任人姓名	responsible_person_name
	@EsField("responsible_person_name")
	@ApiModelProperty(value = "责任人姓名")
	String responsiblePersonName;

//	责任人类型	responsible_person_type
	@EsField("responsible_person_type")
	@ApiModelProperty(value = "责任人类型")
	String responsiblePersonType;

//	责任人所在部门	responsible_person_dept
	@EsField("responsible_person_dept")
	@ApiModelProperty(value = "责任人所在部门")
	String responsiblePersonDept;

//	责任人密级	responsible_person_level
	@EsField("responsible_person_level")
	@ApiModelProperty(value = "责任人密级")
	String responsiblePersonLevel;

}