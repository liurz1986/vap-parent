package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "dimension_table_field")
public class DimensionTableField {

	
	@Id
    @Column
	private String guid;
	@Column(name="field_name")
	private String fieldName;  //字段名称
	@Column(name="field_length")
	private int fieldLength;  //字段长度
	@Column(name="field_type")
	private String fieldType; //字段类型
	@Column(name="field_desc")
	private String fieldDesc; //字段描述
	@Column(name="table_guid")
	private String tableGuid; //关联维表guid
	
	
	@Column(name="format_type")
	private   String  formatType;
	
	
	@Column(name="enum_type")
	private   String  enumType;
	@Column(name="alias_name")
	private   String  aliasName;//别名
	
}
