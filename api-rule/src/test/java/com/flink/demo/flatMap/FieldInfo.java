package com.flink.demo.flatMap;

import com.vrv.rule.ruleInfo.exchangeType.flatMap.RowInfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldInfo {
	private String type;    // pojo, pojoArray, 其他基础类型
	private int order;
	private String colName;
	private RowInfo hints;     // 当type是pojo类型或pojoArray类型的时候，对应其中的类型信息
	
	private FieldInfo flatFieldRef;
	
	public FieldInfo copyOne() {
		FieldInfo build = FieldInfo.builder().type(type).order(order).colName(colName).hints(hints).build();
		flatFieldRef = build;
		return build;
	}
}
