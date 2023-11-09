package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
public class AssetPanel {

	String title;
	String name;
	Boolean canUpdateTitle;
	Boolean canUpdateIndex;
	Boolean canUpdateVisible;
	Integer index;
	Boolean visible;//是否显示
}
