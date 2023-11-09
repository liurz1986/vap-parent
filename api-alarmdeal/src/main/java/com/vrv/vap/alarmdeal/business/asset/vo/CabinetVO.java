package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
/**
 * 机柜VO
 * @author 涂美政
 *
 */
public class CabinetVO {
	
    private String guid;
	private String code;
	private String type;
	private Integer height;
    private String roomGuid;
    private String roomName;
    private Integer marginTop;
    private Integer marginLeft;
    private Integer roomWidth;
    private Integer roomHeight;
    private double positionX;
    private double positionY;
    private double positionZ;
    private Integer moved;
	private String order_;    
	private String by_;   
	private Integer start_;
	private Integer count_;
    
}
