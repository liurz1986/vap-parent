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
@Entity
@Table(name = "filter_source_status")
@Data
public class FilterSourceStatus {
    @Id
    @Column(name="id")
    @ApiModelProperty("记录id")
    private String id;

    @Column(name="data_source_id")
    @ApiModelProperty("数据源ID")
    private Integer dataSourceId;

    @Column(name="open_status")
    @ApiModelProperty("开启状态")
    private Integer openStatus;

    @Column(name="data_status")
    @ApiModelProperty("数据状态")
    private Integer dataStatus;

    @Column(name="msg")
    @ApiModelProperty("信息")
    private String msg;
}
