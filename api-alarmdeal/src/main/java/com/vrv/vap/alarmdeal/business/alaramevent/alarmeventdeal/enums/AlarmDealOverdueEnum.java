package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums;

public enum AlarmDealOverdueEnum {
	OVERDUE("已逾期", -1576800000,1), 
	MIN30("小于30分钟逾期", 1,1800), 
	H4("小于4小时逾期", 1800,14400),
	DAY1("小于1天逾期", 14400,86400);
	
	private String title;
	private Integer differenceStart;
	private Integer differenceEnd;
	
	public String getTitle() {
		return title;
	}


	public Integer getDifferenceStart() {
		return differenceStart;
	}


	public void setDifferenceStart(Integer differenceStart) {
		this.differenceStart = differenceStart;
	}


	public Integer getDifferenceEnd() {
		return differenceEnd;
	}


	public void setDifferenceEnd(Integer differenceEnd) {
		this.differenceEnd = differenceEnd;
	}


	public void setTitle(String title) {
		this.title = title;
	}

 

	AlarmDealOverdueEnum(String title,Integer start,Integer end){
		this.title = title;
		this.differenceStart = start;
		this.differenceEnd = end;
	}

    public static AlarmDealOverdueEnum getAlarmDealOverdueEnum(String title) {
        
    	AlarmDealOverdueEnum[] values = AlarmDealOverdueEnum.values();
        for(AlarmDealOverdueEnum item : values) {
            if(item.getTitle().equals(title)) {
				return item;
			}
        }
        return null;
    }
}
