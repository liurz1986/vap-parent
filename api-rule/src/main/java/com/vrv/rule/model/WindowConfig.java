package com.vrv.rule.model;

import lombok.Data;

/**
 * 窗口类型配置
 * @author wd-pc
 *
 */
@Data
public class WindowConfig {
    
	private String type; //（窗口附件类型）time,count,session,global
	private Long timeValue;
	private Long timeSlideValue; //滑行时间
	private String timeUnit; //窗口单位
	private String timeSlideUnit; //输出时间单位
	private Long count; //个数窗口大小
	private Long countSlide; //输出窗口大小
	private String timeAttr; //时间类型
}
