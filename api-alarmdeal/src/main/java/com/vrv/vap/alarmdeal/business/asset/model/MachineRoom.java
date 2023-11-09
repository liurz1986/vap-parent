package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MachineRoom entity. @author Tu Meizheng
 */
@Table(name="machineroom")
@Data
@Entity
@ApiModel(value="机房表")
public class MachineRoom implements java.io.Serializable {

    /**
     * 机房表
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="guid")
    private String guid;
    @Column(name="code")
    private String code;
    @Column(name="width")
    private Integer width;
    @Column(name="height")
    private Integer height;
    @Column(name="sort")
    private Integer sort;
    @Column(name="openMonitor")
    private Integer openMonitor;
    @Column(name="pushMonitor")
    private Integer pushMonitor;
    @Column(name="openAnalysis")
    private Integer openAnalysis;
    @Column(name="pushAnalysis")
    private Integer pushAnalysis;
    @Column(name="showWall")
    private Integer showWall;
}
