package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 上报kafka基类
 */
@Data
public abstract class AbstractUpAssist {

	/**
	 * 申请单位名称
	 */
	@SerializedName(value = "apply_unit", alternate = {"applyUnit"})
	private String applyUnit="";
	/**
	 * 所需协助单位名称
	 */
	@SerializedName(value = "assist_unit", alternate = {"assistUnit"})
	private String assistUnit="";

	/**
	 * 协办事件简要描述
	 */
	@SerializedName(value = "event_describe", alternate = {"noticeDesc"})
	private String eventDescribe="";

}
