package com.vrv.rule.vo;

import java.util.List;

import org.apache.flink.types.Row;

import lombok.Data;

@Data
public class RowVO {
     
	private List<FieldInfoVO> fields;
	private Row row;
}
