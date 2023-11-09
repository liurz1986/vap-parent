package com.vrv.vap.alarmdeal.business.analysis.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
public class DimensionTableFieldVo {

	private String name;  //字段名称
	private String type; //字段类型
	private   String  aliasName;//别名
	
}
