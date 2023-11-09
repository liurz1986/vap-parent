package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name="myticket_template")
public class MyticketTemplate {
	@Id
	private String guid;
	private String name;
	@Column(name="form_data",columnDefinition = "text")
	private String formData;  // 直接的表单信息
	@Column(name="delete_flag")
	private boolean deleteFlag;  // 标记删除
	// 工单2.0新增字段 2022-04-20
	@Column(name = "form_version")
	private String formVersion; //表单版本
}
