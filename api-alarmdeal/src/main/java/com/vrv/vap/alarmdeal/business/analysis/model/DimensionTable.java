package com.vrv.vap.alarmdeal.business.analysis.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

import javax.persistence.Column;

@Data
public class DimensionTable {

	private String cnTableName;   //中文名
	private String enTableName; //英文名
	private List<DimensionTableFieldVo> fields; //对应属性

	@Column(name="table_type")
	private String tableType;   //维表类型   baseline 、 base（不可填参数） 、 other


	@Column(name="baseline_index")
	private String baselineIndex;
	@Column(name="filter_con")
	private String filterCon;  // 条件过滤字段
	private String guid;
	
}
