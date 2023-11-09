package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums;

/**
 * 告警自定义字典表
 * @author wd-pc
 *
 */
public enum AlarmDealDictEnum {

	/**
	 * 时间
	 */
	time("时间","time"),

	/**
	 * 次数
	 */
	count("次数","count");
	
	private String nameCn;
	private String nameEn;
	
	AlarmDealDictEnum(String nameCn,String nameEn){
		this.nameCn = nameCn;
		this.nameEn = nameEn;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	/**
	 * 获得中文自定义名称
	 * @param nameEn
	 * @return
	 */
	public static String getRuleCn(String nameEn){
		String nameCn = null;//默认值
		for (AlarmDealDictEnum assetEnum : AlarmDealDictEnum.values()) {
			String nameEnEnum = assetEnum.getNameEn();
			if(nameEnEnum.equals(nameEn)){
				nameCn = assetEnum.getNameCn();
				break;
			}
		}
		return nameCn;
	}
	
	
	
	
	
	
}
