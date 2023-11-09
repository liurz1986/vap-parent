package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;

import lombok.Data;

@Data
public class AlarmEventUrgeVO extends AlarmEventUrge {
	String eventId;
	   @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	Date  eventCreattime;
}
