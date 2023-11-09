package com.vrv.vap.alarmdeal.business.asset.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SafeSecretProduceVO {
    private String guid;
    // 产品名称
    private String name;
    // 生产厂商
    private String manufacturer;
    // 版本号
    private String version;
    // 关联设备guid
    private String assetGuid;

    @ApiModelProperty(value="排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页
    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
}
