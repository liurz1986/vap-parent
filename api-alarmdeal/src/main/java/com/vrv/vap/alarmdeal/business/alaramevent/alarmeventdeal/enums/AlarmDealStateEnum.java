package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums;

/**
 * 告警处理状态
 * @author sj100d
 *
 */
public enum AlarmDealStateEnum {
	UNTREATED("未处置", 0), 
	PROCESSING("处置中", 1), 
	//SUSPEND_PROCESSING("已挂起", 2),
	PROCESSED("已处置", 3);
	
	
	
	private String title;
	private Integer code;
	
	private AlarmDealStateEnum(String title, int code)
	{
		this.title = title;
		this.code = code;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}


	public static Integer getValue(String value) {
		AlarmDealStateEnum[] businessModeEnums = values();
		for (AlarmDealStateEnum businessModeEnum : businessModeEnums) {
			if (businessModeEnum.getTitle().equals(value)) {
				return businessModeEnum.getCode();
			}
		}
		return null;
	}

	public static String getDesc(Integer value) {
		AlarmDealStateEnum[] businessModeEnums = values();
		for (AlarmDealStateEnum businessModeEnum : businessModeEnums) {
			if (businessModeEnum.getCode().equals(value)) {
				return businessModeEnum.getTitle();
			}
		}
		return null;
	}

    public static AlarmDealStateEnum getAlarmDealStateEnumByCode(int code) {
        
    	AlarmDealStateEnum[] values = AlarmDealStateEnum.values();
        for(AlarmDealStateEnum item : values) {
            if(code==item.getCode().intValue()){
                return item;
			}
        }
        return null;
    }
}
