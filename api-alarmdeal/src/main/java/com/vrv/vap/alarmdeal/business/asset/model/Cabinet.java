package com.vrv.vap.alarmdeal.business.asset.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Cabinet entity. @author Tu Meizheng
 */
@Table(name="cabinet")
@Data
@Entity
@ApiModel(value = "机柜表")
public class Cabinet implements java.io.Serializable {

	/**
	 * 机柜表
	 */
	private static final long serialVersionUID = 1L;
    @Id
    @Column(name="guid")
	private String guid;
	@Column(name="code")
	private String code;
	@Column(name="type")
	private String type;
	@Column(name="height")
	private Integer height;
	@Column(name="roomGuid")
	private String roomGuid;
	@Column(name="marginTop")
    private Integer marginTop;
    @Column(name="marginLeft")
    private Integer marginLeft;
    @Column(name="roomWidth")
    private Integer roomWidth;
    @Column(name="roomHeight")
    private Integer roomHeight;
    @Column(name="positionX")
    private double positionX;
    @Column(name="positionY")
    private double positionY;
    @Column(name="positionZ")
    private double positionZ;
    @Column(name="moved")
	private Integer moved;
}