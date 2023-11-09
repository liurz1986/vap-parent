package com.vrv.vap.admin.common.enums;

/**
 * 模板标识枚举
 * 
 * @author wd-pc
 *
 */
public enum TagEnum {
	/**
	 *
	 */
	tag1("tag1", "template1.ftl"), tag2("tag2", "template2.ftl"), tag3("tag3", "template3.ftl");

	private String key;
	private String value;

	private TagEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static String getValue(String key){
		for (TagEnum t : TagEnum.values()) {
			if(t.getKey().equals(key)){
				return t.value;
			}
		}
		return null;
	}
	
}
