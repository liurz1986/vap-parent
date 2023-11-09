package com.vrv.rule.vo;

import lombok.Data;

@Data
public class EventColumn {
    
	private String guid;//主键Id
	private String fieldName; //名称
	private String fieldType;//类型
	private Integer order; //排序
	private String tableName; //表名
	private String tableId; //表Id
}
