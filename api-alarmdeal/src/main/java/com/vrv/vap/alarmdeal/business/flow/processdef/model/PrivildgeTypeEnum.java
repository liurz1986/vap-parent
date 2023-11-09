package com.vrv.vap.alarmdeal.business.flow.processdef.model;

public enum PrivildgeTypeEnum {
	all("所有成员"), specify("指定成员");

	private String text;

	private PrivildgeTypeEnum(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
