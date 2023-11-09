package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
public class LabelStatusVO {

	String labelName;//英文
	String labelTitle;//中文
	String labelValue;//值
	String labelExplain;//说明
	String updateTime;
}
