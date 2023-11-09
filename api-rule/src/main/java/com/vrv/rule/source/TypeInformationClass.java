package com.vrv.rule.source;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.Types;
import org.apache.flink.types.Row;

import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.FieldTypeEnum;

public class TypeInformationClass {
      
	public static TypeInformation<Row> getTypeInformationTypes(List<FieldInfoVO> fieldInfoList) {
		String[] field = getField(fieldInfoList);
		TypeInformation[] types = getTypeInformation(fieldInfoList);
		TypeInformation<Row> row = Types.ROW(field, types);
		return row;
	}
	
	public static String[] getField(List<FieldInfoVO> fieldInfoList){
		List<String> fieldList = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : fieldInfoList) {
			String fieldName = fieldInfoVO.getFieldName();
			fieldList.add(fieldName);
		}
		String[] field=fieldList.toArray(new String[fieldList.size()]);
	    return field;
	}
	
	/**
	 * 获得对应的匹配类型
	 * @return
	 */
	public static TypeInformation[] getTypeInformation(List<FieldInfoVO> fieldInfoList){
		 List<TypeInformation> list = new ArrayList<>();
		 for (FieldInfoVO fieldInfoVO : fieldInfoList){
				String fieldType = fieldInfoVO.getFieldType();
				switch (fieldType) {
					case "pojoArray":
					case "pojo":
						List<FieldInfoVO> pojoFields = fieldInfoVO.getChildFields();
						TypeInformation[] pojoTypeInformation = getTypeInformation(pojoFields);
						String[] pojoField = getField(pojoFields);
						TypeInformation flinkTypeInformation = FieldTypeEnum.getFlinkType(fieldType,pojoField,pojoTypeInformation);
						list.add(flinkTypeInformation);
						break;
					default:
						TypeInformation typeInformation = FieldTypeEnum.getFlinkType(fieldType);
						list.add(typeInformation);
						break;
				}
		 }
		 TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
		return types;
	 }
}
