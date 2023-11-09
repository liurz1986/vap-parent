package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

/**
 * 描述自定义工单的表单信息
 * @author Administrator
 *
 */
@Embeddable
@Data
public class MyTicketForminfo {
	@Column(name="form_type")
	private String formType;  // inner, template, custom
	@Column(name="inner_guid")
	private String innerGuid;  // 内置表单数据guid
	@ManyToOne
	private MyticketTemplate template; // 模板guid
	@Column(name="form_data",columnDefinition = "text")
	private String formData;    // 直接的表单信息
}
