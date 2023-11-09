package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.vo.ParamsColumns;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ParamsContent {

	
	@ApiModelProperty(value = "参数描述")
	String paramsDesc;
	@ApiModelProperty(value = "按钮类型")
	Integer btnType;// 按钮类型 1 普通类型  2有基线
	@ApiModelProperty(value = "运用于多个子项")
	String tabName;// 运用于多个子项
	@ApiModelProperty(value = "查询参数")
	List<QueryParam> queryParams;// 查询参数
	
	@ApiModelProperty(value = "维表名称")
	String dimensionTableName;

	@ApiModelProperty(value = "参数列")
	ParamsColumns paramsColumns;

	@ApiModelProperty(value = "参数数据")
	ParamsData paramsData;
}