package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.enums;

public enum CoreAppEnum {

	//TODO systemId暂定
	main(new String[]{"11114","11113","11112","11111","11110","11188","11187","11186"}, "main"), 
	work(new String[]{"11113"}, "work"), 
	call(new String[]{"11112"}, "call"),
	sso(new String[]{"11111"}, "sso"),
	data(new String[]{"11110"}, "data");

	
	

	// 成员变量
	private String[] systemIds;
	private String code;

	private CoreAppEnum(String[] systemIds, String code)
	{
		this.systemIds = systemIds;
		this.code = code;
	}

	
	

	public String[] getSystemIds() {
		return systemIds;
	}




	public void setSystemIds(String[] systemIds) {
		this.systemIds = systemIds;
	}




	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
