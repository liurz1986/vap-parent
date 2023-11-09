package com.vrv.rule.vo;

import java.io.Serializable;
import java.util.List;

import com.vrv.rule.ruleInfo.exchangeType.flatMap.RowInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tableId; //表的Id
	private String tableName; //表名
	private String fieldName;  //成员名称
	private String fieldType; //成员类型
	private String aggType; //聚合类型 褶皱属性（folds） or 一般属性（ordinary）
	private Boolean eventTime; //是否是事件时间
	private  Integer order; //顺序位置
	private List<FieldInfoVO> childFields; //子成员变量(数组成员)
	private AggregateOperator expression;   // a.x, a.x+a.y  count, sum(a.x)
	private String ordinaryExpression; //一般属性表达式
	private FieldInfoVO flatFieldRef;
	private RowInfo hints;
	
	public FieldInfoVO copyOne() {
		FieldInfoVO field = new FieldInfoVO();
		field.setFieldType(fieldType);
		field.setOrder(order);
		field.setFieldName(fieldName);
		field.setChildFields(childFields);
		flatFieldRef = field;
		return field;
	}


	

	
	
	
}
