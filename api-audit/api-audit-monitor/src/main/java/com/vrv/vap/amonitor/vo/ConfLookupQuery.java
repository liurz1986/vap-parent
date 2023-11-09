package com.vrv.vap.amonitor.vo;


import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel
@SuppressWarnings("unused")
public class ConfLookupQuery extends Query {
    
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 字段对应类型
     * <p>
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

}