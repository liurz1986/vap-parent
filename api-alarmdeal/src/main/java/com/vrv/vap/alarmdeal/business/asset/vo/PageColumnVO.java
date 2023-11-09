package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
public class PageColumnVO {

	String name;
	String title;
	Integer index;
	String type;
	String attributeType;//系统属性、自定义属性
	Boolean check;
}
