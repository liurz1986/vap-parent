package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssetTemplateVO {

	@Data
	public class Context {
		String title;
		
		String regex;
		String regexType;// 正则类型
		String regexBind;// 正则绑定
		String regexMessage;
		
		String defaultValueType;// 默认值类型：无默认值0 固定值1 动态默认值 2
		String defaultValueBind;// 当为动态默认值时： user (当前登录用户)：1 date（当前时间）：2
		String defaultValue;
		
		String inputMessage;
		String description;
		String descriptionTitle;
		
		Boolean isMust;
		Integer length;


		String name;
		String type;
		Boolean visible;
		
		String panel;
		String attributeType;// 系统属性、自定义属性
	}

	@Data
	public class ItemContext {
		Integer h;
		Integer w;
		String key;
		Integer GridX;
		Integer GridY;
		Context context;
	}

	List<ItemContext> info;

	List<ItemContext> omInfo;

	List<ItemContext> extendInfo;

	public List<CustomSettings> getCustomSettings() {
		List<CustomSettings> result = new ArrayList<>();
		if (this.info != null) {
			info.forEach(a -> {
				if (a.context != null) {
					Context context = a.context;

					CustomSettings customSettings = getCustomSettings(context);
					customSettings.setPanel("info");
					result.add(customSettings);
				}
			});
		}
		if (this.omInfo != null) {
			omInfo.forEach(a -> {
				if (a.context != null) {
					Context context = a.context;

					CustomSettings customSettings = getCustomSettings(context);
					customSettings.setPanel("omInfo");
					result.add(customSettings);
				}
			});
		}
		if (this.extendInfo != null) {
			extendInfo.forEach(a -> {
				if (a.context != null) {
					Context context = a.context;

					CustomSettings customSettings = getCustomSettings(context);
					customSettings.setPanel("extendInfo");
					result.add(customSettings);
				}
			});
		}

		return result;
	}

	// 代码未写完
	private CustomSettings getCustomSettings(Context context) {
		CustomSettings customSettings = new CustomSettings();
		//customSettings.setPanel("info");
		customSettings.setType(context.getType());
		customSettings.setTitle(context.getTitle());
		customSettings.setVisible(false);
		customSettings.setName(context.getName());
		customSettings.setIsMust(context.getIsMust());
		customSettings.setLength(context.getLength());
		
		customSettings.setRegex(context.getRegex());
		customSettings.setRegexBind(context.getRegexBind());
		customSettings.setRegexMessage(context.getRegexMessage());
		customSettings.setRegexType(context.getRegexType());
		
		customSettings.setAttributeType("custom");
		
		customSettings.setInputMessage(context.getInputMessage());
		customSettings.setDescription(context.getDescription());
		customSettings.setDescriptionTitle(context.getDescriptionTitle());
		
		customSettings.setDefaultValue(context.getDefaultValue());
		customSettings.setDefaultValueBind(context.getDefaultValueBind());
		customSettings.setDefaultValueType(context.getDefaultValueType());
		
		return customSettings;
	}

}
