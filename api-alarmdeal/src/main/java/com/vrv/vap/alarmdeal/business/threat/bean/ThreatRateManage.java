package com.vrv.vap.alarmdeal.business.threat.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 梁国露
 * @since: 2022/11/2 16:33
 * @description:
 */
@Data
@Table(name="threat_rate_manage")
@Entity
@ApiModel(value="威胁频率配置表")
public class ThreatRateManage {
    @Id
    @Column(name="guid")
    @ApiModelProperty(value="主键id")
    private String guid;

    @Column(name="start_day_count")
    @ApiModelProperty(value="开始天数")
    private Integer startDayCount;

    @Column(name="end_day_count")
    @ApiModelProperty(value="结束天数")
    private Integer endDayCount;

    @Column(name="threat_rate_rank")
    @ApiModelProperty(value="威胁频率等级")
    private Integer threatRateRank;

    @Column(name="desc")
    @ApiModelProperty(value="描述")
    private String desc;

}
