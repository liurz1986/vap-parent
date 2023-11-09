package com.vrv.vap.alarmdeal.business.asset.vo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import lombok.Data;

@Data
public class AssetSystemAttributeSettingsVO {
 
	private String name;
	private String type;
	private Boolean visible;
	private String panel;
 
	private SystemControlSettings systemSettings;

 
	private CustomSettings customSettings;

	
public AssetSystemAttributeSettingsVO() {
	
}
  public AssetSystemAttributeSettingsVO(AssetSystemAttributeSettings setting)
	{
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
		this.setName(setting.getName());
		this.setPanel(setting.getPanel());
		this.setVisible(setting.getVisible());
		this.setType(setting.getType());

		CustomSettings custom = gson.fromJson(setting.getCustomSettings(), CustomSettings.class);
		this.setCustomSettings(custom);

		SystemControlSettings systemSettings = gson.fromJson(setting.getSystemSettings(), SystemControlSettings.class);
		this.setSystemSettings(systemSettings);
	}
}
