package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 工作流内置表单
 * @author lijihong
 *
 */
@Entity
@Data
@Table(name="myticket_inner_form")
public class MyticketInnerForm {
	@Id
	private String guid;
	@Column(name="parent_id")
	private String parentId;
	@Column(name="text")
	private String text;
	@Column(name="process_desc")
	private String processDesc;
	@Column(name="formtype")
	private String formtype;  // 过期，没用的字段
	@Column(name="form_infos_guid")
	private String formInfosGuid;
}
