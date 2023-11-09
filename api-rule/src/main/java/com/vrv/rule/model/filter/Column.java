package com.vrv.rule.model.filter;

import java.util.List;

import lombok.Data;

/**
 * talbes列名
 * @author wd-pc
 *
 */
@Data
public class Column {
   
	private String id; //列的id
	private String name; //英文名称
	private String label; //中文名称
	private String exp; //表达式
	private String aggType; //褶皱属性（folds） or 一般属性（ordinary）
	private Integer order; //顺序
	private String dataType; //数据类型
	private String dataHint; //当datatype为pojo或者是pojoArray的时候，对应的类型tableId
	private Boolean eventTime; //是否是事件时间
	private List<Column> childColumn; //子表Column(用于pojo和pojoArray)
}
