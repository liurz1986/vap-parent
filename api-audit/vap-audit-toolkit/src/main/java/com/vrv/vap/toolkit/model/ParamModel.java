package com.vrv.vap.toolkit.model;

/**
 * Created by lizj on 2019/10/14.
 */
public class ParamModel {
    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 字段说明
     */
    private String fieldDescription;
    /**
     * 字段值
     */
    private Object fieldValue;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }
}
