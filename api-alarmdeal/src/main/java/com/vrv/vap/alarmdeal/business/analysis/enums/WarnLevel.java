package com.vrv.vap.alarmdeal.business.analysis.enums;

public enum WarnLevel
{
	
	LOW("低级", 1), MIDDLE_LOW("中低级", 2), MIDDLE("中级", 3), MIDDLE_HIGH("中高级", 4), HIGH("高级", 5);

	
	// 成员变量
	private String name;
	private int index;

	private WarnLevel(String name, int index)
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

	// 普通方法
	public static String getName(String strIndex)
	{
		int index = 1 ;
    	if("".equals(strIndex) || null == strIndex || "1".equals(strIndex))
    	{
    		return LOW.getName();
    	}
    	else
    	{
    		index = Integer.parseInt(strIndex);
    	}
		for (WarnLevel c : WarnLevel.values())
		{
			if (c.getIndex() == index)
			{
				return c.name;
			}
		}
		return null;
	}
}
