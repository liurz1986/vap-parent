package com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 梁国露
 * @since: 2023/3/22 17:56
 * @description:
 */
@Data
public class FilterSourceStatusInfo {
    @ApiModelProperty("记录id")
    private String id;

    @ApiModelProperty("数据源ID")
    private Integer dataSourceId;

    @ApiModelProperty("数据topic名称")
    private String dataTopicName;

    @ApiModelProperty("开启状态")
    private Integer open_status;

    @ApiModelProperty("数据状态")
    private Integer data_status;

    @ApiModelProperty("信息")
    private String msg;

    @ApiModelProperty("数据时间")
    private String insertTime;

    @ApiModelProperty("索引")
    private String index;

}
