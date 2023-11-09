package com.vrv.vap.toolkit.model;

public class ExcelEnumModel {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 英文字段名
     */
    private String[] fieldsEn;

    /**
     * 中文字段名
     */
    private String[] fieldsCn;

    /**
     * 导入时校验最大长度
     */
    private int[] maxlength;

    /**
     * 导入时校验最大长度
     */
    private int[] isNull;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String[] getFieldsEn() {
        return fieldsEn;
    }

    public void setFieldsEn(String[] fieldsEn) {
        this.fieldsEn = fieldsEn;
    }

    public String[] getFieldsCn() {
        return fieldsCn;
    }

    public void setFieldsCn(String[] fieldsCn) {
        this.fieldsCn = fieldsCn;
    }

    public int[] getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(int[] maxlength) {
        this.maxlength = maxlength;
    }

    public int[] getIsNull() {
        return isNull;
    }

    public void setIsNull(int[] isNull) {
        this.isNull = isNull;
    }
}
