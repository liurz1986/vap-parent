package com.vrv.rule.model;

import java.io.Serializable;

import com.vrv.rule.vo.LogicOperator;

import lombok.Data;
/**
 * 维表配置信息
 * @author wd-pc
 *
 */

@Data
public class DimensionConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dimensionTableName; //关联维表表名
	private String dimensionFieldName; //关联选择字段，逗号分割
	private String dimensionAliasName;//关联选择字段别名，逗号分割
	private String filterCon;//过滤条件
	private String highLevelSearchCon;//高级搜索条件
	private LogicOperator loginExp;  //逻辑表达式
	private boolean matchOutput;   //匹配开关
	private String tableType;   //维表类型   baseline 、 base（不可填参数） 、 other
	
}
