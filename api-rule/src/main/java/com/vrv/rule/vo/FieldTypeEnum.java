package com.vrv.rule.vo;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.Types;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月22日 上午11:53:32 
* 类说明    原始日志字段枚举
*/
public  enum  FieldTypeEnum {
   
	
	Varchar_Name("varchar",Types.STRING()),
	Integer_Name("bigint",Types.LONG()),
	Double_Name("double",Types.DOUBLE()),
	Float_Name("float",Types.FLOAT()),
	Boolean_Name("boolean",Types.BOOLEAN()),
	Int_Name("int",Types.INT()),
	Date_Name("datetime",Types.SQL_TIMESTAMP()),
	String_Name("java.lang.String",Types.STRING()),
	Long_Name("java.lang.Long",Types.LONG()),
	TimeStamp_Name("java.sql.Timestamp",Types.SQL_TIMESTAMP()),
	String_Array("stringArray",Types.OBJECT_ARRAY(Types.STRING())),
	Long_Array("longArray",Types.PRIMITIVE_ARRAY(Types.LONG())),
	Integer_Array("integerArray",Types.PRIMITIVE_ARRAY(Types.INT())),
	Double_Array("doubleArray",Types.PRIMITIVE_ARRAY(Types.DOUBLE())),
	Float_Array("floatArray",Types.PRIMITIVE_ARRAY(Types.FLOAT())),
	Byte_Array("byteArray",Types.PRIMITIVE_ARRAY(Types.BYTE())),
	Map_Array("mapArray",Types.MAP(Types.STRING(), Types.OBJECT_ARRAY(Types.STRING()))),

	MAP_PRIMITIVE_ARRAY("mapPrimitiveArray",Types.OBJECT_ARRAY(Types.MAP(Types.STRING(),Types.STRING()))),

	Map_Map("mapMap",Types.MAP(Types.STRING(), Types.MAP(Types.STRING(), Types.STRING()))),
	Map("map",Types.MAP(Types.STRING(), Types.STRING()));
	
	
	private String columnTypeField;
	private TypeInformation flinkType;
	
	public String getColumnTypeField() {
		return columnTypeField;
	}

	public void setColumnTypeField(String columnTypeField) {
		this.columnTypeField = columnTypeField;
	}



	public TypeInformation getFlinkType() {
		return flinkType;
	}



	public void setFlinkType(TypeInformation flinkType) {
		this.flinkType = flinkType;
	}



	FieldTypeEnum(String columnTypeField,TypeInformation flinkType){
		this.columnTypeField = columnTypeField;
		this.flinkType = flinkType;
	}
	
	
	
	/**
	 * 根据导入文件title找到对应的field
	 * @param title
	 * @return
	 */
	public static TypeInformation getFlinkType(String sqlField){
		TypeInformation flinkType = null;
		for (FieldTypeEnum fieldEnum : FieldTypeEnum.values()) {
			String columnTypeField = fieldEnum.getColumnTypeField();
			if(sqlField.equals(columnTypeField)){
				flinkType = fieldEnum.getFlinkType();
				break;
			}
		}
		
		return flinkType;
	}
	
	
	public static TypeInformation getFlinkType(String sqlField,String[] fieldType,TypeInformation[] typeInformationTypes){
		TypeInformation flinkType = null;
		switch (sqlField) {
		case "pojoArray": //pojo数组类型
			flinkType =Types.OBJECT_ARRAY(Types.ROW(fieldType,typeInformationTypes));
			break;
		case "pojo": //实体类型
			flinkType =Types.ROW(fieldType, typeInformationTypes); 
			break;	
		default:
			for (FieldTypeEnum fieldEnum : FieldTypeEnum.values()) {
				String columnTypeField = fieldEnum.getColumnTypeField();
				if(sqlField.equals(columnTypeField)){
					flinkType = fieldEnum.getFlinkType();
					break;
				}
			}
		}
		return flinkType;
	}
	
	
	
}
