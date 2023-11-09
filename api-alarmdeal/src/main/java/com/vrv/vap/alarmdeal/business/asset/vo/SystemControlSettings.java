package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
public  class  SystemControlSettings
{

	private Boolean canUpdateTitle;
	private Boolean canUpdateVisible;
	private Boolean canUpdateMust;
	private Boolean canUpdateLength;
	private Boolean canUpdateDefaultValue;
	private Boolean canUpdateInputMessage;
	private Boolean canUpdateDescription;
	private int lengthMax;
	private int lengthMix;

}
