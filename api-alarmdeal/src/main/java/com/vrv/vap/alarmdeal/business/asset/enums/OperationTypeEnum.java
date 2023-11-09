package com.vrv.vap.alarmdeal.business.asset.enums;

public enum OperationTypeEnum {
	ADD(1, "新增"),
	DELETE(2, "删除"),
	UPDATE(3,"修改");

	private Integer code;
	private String name;
	
	OperationTypeEnum(Integer code, String name) {
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
