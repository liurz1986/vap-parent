package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums;

public enum AlarmDealTypeEnum
{
	
	WAITE_SURE("待确认", 0), 
	DEAL_ING("处置中", 1),  //处置中
	ERROR_REPORT("误报", 2), //已处置
	ALREADY_SURE("已确认", 3), // 处理中
	ANALYSIS_ING("分析中", 4),// 处理中
	ALREADY_DEAL("已处置",5),//已处置
	END("归档",6),//已处置
	INTERRUPT("阻断",7),// 已处置
	IGNORE("忽略",8);//已处置

	
	// 成员变量
	private String name;
	private int index;

	private AlarmDealTypeEnum(String name, int index)
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
