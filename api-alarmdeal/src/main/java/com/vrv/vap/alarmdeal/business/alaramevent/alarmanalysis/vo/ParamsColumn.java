package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import java.util.List;

import com.vrv.vap.jpa.web.NameValue;

import lombok.Data;

@Data
public class ParamsColumn {

	String title;
	String dataIndex;
	String type;
	//表示该字段是否可以转换
	List<NameValue> items;
}
