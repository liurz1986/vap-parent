package com.vrv.vap.alarmdeal.business.threat.bean;

import com.vrv.vap.es.model.PrimaryKey;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: Administrator
 * @since: 2022/8/29 15:39
 * @description:
 */
@Entity
@Data
@Table(name = "threat_level_manage")
@PrimaryKey("guid")
public class ThreatLevelManage {
    @Id
    @Column(name = "guid")
    @ApiModelProperty("guid")
    private String guid;

    @Column(name = "start_threat_value")
    @ApiModelProperty("开始威胁值")
    private Integer startThreatValue;

    @Column(name = "end_threat_value")
    @ApiModelProperty("结束威胁值")
    private Integer endThreatValue;

    @Column(name = "threat_level")
    @ApiModelProperty("威胁程度等级")
    private Integer threatLevel;

    @Column(name = "desc")
    @ApiModelProperty("描述")
    private String desc;
}
