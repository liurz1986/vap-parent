package com.vrv.vap.syslog.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wh1107066
 * @date 2021/7/4 11:30
 */
@ApiModel(value = "字段描述")
public class FieldDescrDTO {
    @ApiModelProperty(value = "字段名称，与对象属性一致")
    private String name;
    @ApiModelProperty(value = "字段描述信息")
    private String description;
    /**
     * 是否脱敏 false 关闭，true 开启脱敏
     */
    @ApiModelProperty(value = "开启脱敏")
    private Boolean desensitization;

    public FieldDescrDTO(String name, String description, Boolean desensitization) {
        this(name);
        this.description = description;
        this.desensitization = desensitization;
    }

    public FieldDescrDTO(String name) {
        this.name = name;
    }

    public FieldDescrDTO() {
    }

    @Override
    public String toString() {
        return "FieldDescrDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", desensitization=" + desensitization +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDesensitization() {
        return desensitization;
    }

    public void setDesensitization(Boolean desensitization) {
        this.desensitization = desensitization;
    }
}
