package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

@Data
public class DimensionTableColumn {
	String columnName;
	String columnType;
	Boolean isMust;
	String characterMaximumLength;
	String columnComment;
}



