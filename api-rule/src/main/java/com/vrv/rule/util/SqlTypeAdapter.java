package com.vrv.rule.util;

import java.sql.Types;

public enum SqlTypeAdapter {
	
	BIGINT_TYPE(Types.BIGINT,"bigint"),
	TIMESTAMP_TYPE(Types.TIMESTAMP,"timestamp"),
	DATE_TYPE(Types.DATE,"date"),
	TINYINT_TYPE(Types.TINYINT,"tinyint"),
	VARHAR_TYPE(Types.VARCHAR,"varchar"),
	DOUBLE_TYPE(Types.DOUBLE,"double"),
	FLOAT_TYPE(Types.FLOAT,"float"),
	INTEGER_TYPE(Types.INTEGER,"int");
	
	private Integer columnTypeField;
	private String sqlType;
	
	SqlTypeAdapter(Integer columnTypeField,String sqlType) {
		this.columnTypeField = columnTypeField;
		this.sqlType = sqlType;
		
	}
	public Integer getColumnTypeField() {
		return columnTypeField;
	}
	public void setColumnTypeField(Integer columnTypeField) {
		this.columnTypeField = columnTypeField;
	}
	public String getSqlType() {
		return sqlType;
	}
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}
	
	
	/**
	 * 根据导入文件title找到对应的field
	 * @param title
	 * @return
	 */
	public static String getSqlType(Integer sqlField){
		String sqlType= "varchar";
		for (SqlTypeAdapter sqlTypeAdapter : SqlTypeAdapter.values()) {
			Integer typeField = sqlTypeAdapter.getColumnTypeField();
			if(typeField.equals(sqlField)){
				sqlType = sqlTypeAdapter.getSqlType();
				break;
			}
		}
		return sqlType;
	}
	
	
	
	
}
