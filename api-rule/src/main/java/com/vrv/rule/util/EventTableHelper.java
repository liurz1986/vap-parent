package com.vrv.rule.util;

import java.util.List;

import com.vrv.rule.vo.EventColumn;
import com.vrv.rule.vo.FieldInfoVO;

public class EventTableHelper {

	public static EventColumn getFieldsInfo(String tableName, String fieldName,List<FieldInfoVO> inputFieldInfoVOs) {
		//TODO获得对应的数据
		EventColumn eventColumn = new EventColumn();
		for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
			String fieldName2 = fieldInfoVO.getFieldName();
			String tableName2 = fieldInfoVO.getTableName();
			if(fieldName2.equals(fieldName)&&tableName2.equals(tableName)){
				String fieldType = fieldInfoVO.getFieldType();
				eventColumn.setFieldName(fieldName);
				eventColumn.setTableName(tableName);
				eventColumn.setFieldType(fieldType);
				eventColumn.setOrder(fieldInfoVO.getOrder());
				return eventColumn;
			}
		}
		throw new RuntimeException("没有找到对应的eventColumn，请检查！");
	}

}
