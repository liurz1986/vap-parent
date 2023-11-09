package com.vrv.vap.monitor.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "MonitorAssetInfo分类统计对象")
public class MonitorAssetGeneral<T> {

    @ApiModelProperty(value = "资产分类")
    private String type;

    @ApiModelProperty(value = "连通数")
    private long connectCount;

    @ApiModelProperty(value = "开启监控的数量")
    private long startUpCount;

    @ApiModelProperty(value = "总数量")
    private long totalCount;

    @ApiModelProperty(value = "资产列表")
    private List<T> data;
}
