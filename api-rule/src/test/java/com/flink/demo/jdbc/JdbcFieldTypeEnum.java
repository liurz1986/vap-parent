package com.flink.demo.jdbc;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.Types;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月22日 上午11:53:32 
* 类说明    原始日志字段枚举
*/
public enum JdbcFieldTypeEnum {
   
	
	Varchar_Name("varchar",BasicTypeInfo.STRING_TYPE_INFO),
	Integer_Name("bigint",BasicTypeInfo.BIG_INT_TYPE_INFO),
	Double_Name("double",BasicTypeInfo.DOUBLE_TYPE_INFO),
	Float_Name("float",BasicTypeInfo.FLOAT_TYPE_INFO),
	Boolean_Name("tinyint",BasicTypeInfo.BOOLEAN_TYPE_INFO),
	Int_Name("int",BasicTypeInfo.INT_TYPE_INFO),
	Date_Name("date",BasicTypeInfo.DATE_TYPE_INFO);
	
	private String columnTypeField;
	private TypeInformation<?> flinkType;
	
	public String getColumnTypeField() {
		return columnTypeField;
	}

	public void setColumnTypeField(String columnTypeField) {
		this.columnTypeField = columnTypeField;
	}



	public TypeInformation<?> getFlinkType() {
		return flinkType;
	}



	public void setFlinkType(TypeInformation<?> flinkType) {
		this.flinkType = flinkType;
	}



	JdbcFieldTypeEnum(String columnTypeField,TypeInformation<?> flinkType){
		this.columnTypeField = columnTypeField;
		this.flinkType = flinkType;
	}
	
	
	
	/**
	 * 根据导入文件title找到对应的field
	 * @param title
	 * @return
	 */
	public static TypeInformation<?> getFlinkType(String sqlField){
		TypeInformation<?> flinkType = null;
		for (JdbcFieldTypeEnum fieldEnum : JdbcFieldTypeEnum.values()) {
			String columnTypeField = fieldEnum.getColumnTypeField();
			if(sqlField.equals(columnTypeField)){
				flinkType = fieldEnum.getFlinkType();
				break;
			}
		}
		return flinkType;
	}
	
}
