package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 事件信息对象
 */
@Data
public class EventInfo {
	@SerializedName(value = "eventId", alternate = {"event_id"})
	private String eventId;
	@SerializedName(value = "noticeDesc",alternate = {"notice_desc"})
	private String noticeDesc;

}
