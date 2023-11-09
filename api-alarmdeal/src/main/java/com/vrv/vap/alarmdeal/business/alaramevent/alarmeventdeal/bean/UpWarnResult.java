package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 预警上报反馈
 */
@Data
public class UpWarnResult {
	@SerializedName(value = "warnning_description",alternate = {"taskDesc"})
	private String warnning_description;
	@SerializedName(value = "warnning_conlusion",alternate = {"responseNote"})
	private String warnning_conlusion;
}
