package com.vrv.rule.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 输出配置
 * @author wd-pc
 *
 */
@Data
public class Configs implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name; //kafka topic名称
    private AlarmObj alarmObj; //告警对象
    private List<EventField> eventField;  //事件对象属性

	
}
