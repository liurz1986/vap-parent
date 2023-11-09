package com.vrv.vap.xc.model;


import com.vrv.vap.toolkit.annotations.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel
@SuppressWarnings("unused")
public class ConfLookupModel {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 字段对应类型
     * system:系统
     * website:站点
     * device:设备
     * prealarm:预警
     * alarm:报警
     */
    @ApiModelProperty("字段对应类型, system:系统 website:站点 device:设备 prealarm:预警 alarm:报警")
    private String type;

    /**
     * 字段对应编码
     */
    @ApiModelProperty("字段对应编码")
    @NotNull
    private String code;

    /**
     *
     */
    @ApiModelProperty("")
    private String value;

    /**
     *
     */
    @ApiModelProperty("")
    private String description;

    /**
     * 状态
     * 1:启用(默认)
     * 0:禁用
     */
    @ApiModelProperty("状态 1:启用(默认) 0:禁用")
    private Byte status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}