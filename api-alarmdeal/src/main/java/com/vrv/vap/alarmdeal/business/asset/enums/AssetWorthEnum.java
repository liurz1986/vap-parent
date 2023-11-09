package com.vrv.vap.alarmdeal.business.asset.enums;

public enum AssetWorthEnum {
	VERYLOW(1, "很低"),
	LOW(2, "低"),
	CENTRE(3, "中"),
	HIGH(4, "高"),
	VERYHIGH(5,"很高");

	private Integer code;
	private String name;

	AssetWorthEnum(Integer code, String name) {
		this.code = code;
		this.name = name;
	}
 
	public Integer getCode() {
		return code;
	}

 
	public String getName() {
		return name;
	}
}
