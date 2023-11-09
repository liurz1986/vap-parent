package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums;
/**
 * 告警风险等级
 * @author sj100d
 *
 */
public enum AlarmRiskLevelEnum {
	ONE("较低", 1),
	TWO("一般", 2),
	THREE("重要", 3),
	FOUR("严重", 4),
	FIVE("紧急", 5);
	
	
	
	private String title;
	private Integer code;
	
	private AlarmRiskLevelEnum(String title, int code)
	{
		this.title = title;
		this.code = code;
	}

	public static Integer getValue(String value) {
		AlarmRiskLevelEnum[] businessModeEnums = values();
		for (AlarmRiskLevelEnum businessModeEnum : businessModeEnums) {
			if (businessModeEnum.getTitle().equals(value)) {
				return businessModeEnum.getCode();
			}
		}
		return null;
	}

	public static String getDesc(Integer value) {
		AlarmRiskLevelEnum[] businessModeEnums = values();
		for (AlarmRiskLevelEnum businessModeEnum : businessModeEnums) {
			if (businessModeEnum.getCode().equals(value)) {
				return businessModeEnum.getTitle();
			}
		}
		return null;
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
}
