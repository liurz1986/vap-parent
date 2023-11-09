package com.vrv.vap.alarmdeal.business.analysis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 	区域坐标关联
 * @author wd-pc
 *
 */
@Entity
@Table(name = "area_location")
@Data
public class AreaLocation {
    @Id
    @Column(name="guid")
	private String guid;
    @Column(name="areaName") 
	private String areaName;
    @Column(name="location")
    private String location;
}
