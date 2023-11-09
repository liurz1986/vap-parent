package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo;

import java.util.List;

import lombok.Data;

@Data
public class LogIdVO {

	//索引名称
	String indexName;
	//eventTableName
	String eventTableName;
	String eventTableGuid;
	//集合
	List<String>  ids;
	List<String>  logGuids;//原始guid
}
