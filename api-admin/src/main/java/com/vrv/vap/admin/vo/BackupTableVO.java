package com.vrv.vap.admin.vo;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class BackupTableVO {
    private String name;
    private String[] dateFields;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDateFields() {
        return dateFields;
    }

    public void setDateFields(String[] dateFields) {
        this.dateFields = dateFields;
    }

    public String toString(){
        return "tableName "+name+",date fields "+dateFields==null?"none": StringUtils.join(dateFields,',');
    }
}
