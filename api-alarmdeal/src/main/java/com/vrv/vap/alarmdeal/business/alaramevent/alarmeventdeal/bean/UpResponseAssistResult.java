package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 上报协办处置结果信息
 */
@Data
public class UpResponseAssistResult extends AbstractUpAssist {
	/**
	 * 反馈结果
	 */
	@SerializedName(value = "conlusion", alternate = {"responseNote"})
	private String conlusion;
	/**
	 * 协查编码（暂定）
	 */
	private String apply_id="";

}
