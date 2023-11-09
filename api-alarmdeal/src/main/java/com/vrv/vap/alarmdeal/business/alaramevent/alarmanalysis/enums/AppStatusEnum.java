package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.enums;

public enum AppStatusEnum {

	
	normal("正常", 100), 
	threat("威胁", 50), 
	fall("失陷", 0);
	
	

	
	// 成员变量
	private String name;
	private int index;

	private AppStatusEnum(String name, int index)
	{
		this.name = name;
		this.index = index;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

}
