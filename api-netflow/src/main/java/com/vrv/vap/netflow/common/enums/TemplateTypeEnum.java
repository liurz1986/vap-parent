package com.vrv.vap.netflow.common.enums;

/**
 * @author lilang
 * @date 2022/3/31
 * @description
 */
public enum TemplateTypeEnum {

    TYPE_XLS(1,"excel"),
    TYPE_XML(2,"xml");

    private Integer code;
    private String name;

    TemplateTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
