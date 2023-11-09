package com.vrv.vap.admin.vo.supervise;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AnnounceDataInfo {
	String id;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date createTime;
	String noticeType;
	String noticeName;
	String noticeDesc;
	
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date sendTime;
	String  event_id;
	String attachment;
	String fileName;
}
