package com.vrv.vap.alarmdeal.business.analysis.enums;

public enum WarnType
{
	SAFE_NOTICE("安全通告", 1), ATTACK_NOTICE("攻击预警", 2), LOOPHOLE_NOTICE("漏洞预警", 3), VIRUSES_NOTICE("病毒预警", 4);

	// 成员变量
	private String name;
	private int index;
	
	private WarnType(String name, int index)
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
    public static String getName(String strIndex) {  
    	int index = 1 ;
    	if("".equals(strIndex) || null == strIndex || "1".equals(strIndex))
    	{
    		return SAFE_NOTICE.getName();
    	}
    	else
    	{
    		index = Integer.parseInt(strIndex);
    	}
        for (WarnType c : WarnType.values()) {  
            if (c.getIndex() == index) {  
                return c.name;  
            }  
        }  
        return null;  
    }
	
    
 // 普通方法  
    public static String getIndexs(String strName) {  

    	if("".equals(strName) || null == strName || "1".equals(strName))
    	{
    		return SAFE_NOTICE.getName();
    	}
    	
        for (WarnType c : WarnType.values()) 
        {  
            if (strName.equals(c.getName())) 
            {  
                return c.name;  
            }  
        }  
        return null;  
    }
	
}
