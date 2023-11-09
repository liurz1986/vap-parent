package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 申请协助，我向他人求助，注意方向
 * 1.上报协办请求信息（A  上报  上级）
 */
@Data
public class UpApplyAssist extends AbstractUpAssist {
	/**
	 * 事件id
	 */
	@SerializedName(value = "event_id", alternate = {"eventId"})
	private String event_id;
	/**
	 * 协办说明
	 */
	@SerializedName(value = "description", alternate = {"taskDesc"})
	private String description;
	/**
	 * 协查编码 就是协办id,主键
	 */
	@SerializedName(value = "assis_id", alternate = "guid")
	private String assis_id;
}
