package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssetAttributeTreeNode {
	private String name;
	private String type;
	private String title;
	private Integer index;
	private Object attribute;
	private List<AssetAttributeTreeNode> children;
	
	public AssetAttributeTreeNode() {
		
	}
	
	public AssetAttributeTreeNode(AssetPanel panel)
	{
		this.children=new ArrayList<>();
		this.name=panel.getName();
		this.type="panel";
		this.index=panel.getIndex();
		this.attribute=panel;
		this.title=panel.getTitle();
	}
	
	
	public AssetAttributeTreeNode(AssetSystemAttributeSettingsVO settings,int index)
	{
		CustomSettings customSettings = settings.getCustomSettings();
		this.children=null;
		this.name=settings.getName();
		this.type="attribute";
		
		this.index=index;
		this.attribute=settings;
		this.title=customSettings.getTitle();
	}
}
