package com.vrv.vap.alarmdeal.business.asset.vo;

import com.vrv.vap.jpa.web.NameValue;
import lombok.Data;

import java.util.List;

@Data
public class CustomSettings{
	
	String 	title;
	String regex;
	String regexType;//正则类型
	String regexBind;//正则绑定
	String regexMessage;
	String defaultValueType;//默认值类型：无默认值0   固定值1 动态默认值 2 
	String defaultValueBind;//当为动态默认值时：    user (当前登录用户)：1    date（当前时间）：2
	Object 	defaultValue;
	Boolean isMust;
	Integer length;
	String inputMessage;
	String description;
	String descriptionTitle;
	String name;
	String type;
	Boolean visible;
	String panel;
	String attributeType;//系统属性、自定义属性
	
	
	List<NameValue> option;  //name 为显示值，value为存储值
	
	List<CustomSettings> childrenControl;
}
