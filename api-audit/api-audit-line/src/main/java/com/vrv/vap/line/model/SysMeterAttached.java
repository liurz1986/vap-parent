package com.vrv.vap.line.model;

public class SysMeterAttached {
    private String fieldType;
    private String fieldName;
    private Integer fieldLength;
    private Integer decimalPoint;
    private boolean primaryKey;
    private boolean notNull;
    private boolean autoIncrement;
    private String fieldRemark;

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return "`"+fieldName+"`";
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    public Integer getDecimalPoint() {
        return decimalPoint;
    }

    public void setDecimalPoint(Integer decimalPoint) {
        this.decimalPoint = decimalPoint;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getFieldRemark() {
        return fieldRemark;
    }

    public void setFieldRemark(String fieldRemark) {
        this.fieldRemark = fieldRemark;
    }
}
