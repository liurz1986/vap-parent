package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_discover_entity")
@ApiModel(value = "探索实例定义")
public class DiscoverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("实体名称")
    private String name;

    @ApiModelProperty("实体描述")
    private String description;

    @ApiModelProperty("搜索提示")
    private String tip;

    @ApiModelProperty("图标路径")
    private String icon;

    @ApiModelProperty("输入格式正则")
    private String reg;

    @ApiModelProperty("内置类型 1：身份证，2：ip地址，3：系统编号")
    @Column(name = "built_in_type")
    private Byte builtInType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public Byte getBuiltInType() {
        return builtInType;
    }

    public void setBuiltInType(Byte builtInType) {
        this.builtInType = builtInType;
    }
}