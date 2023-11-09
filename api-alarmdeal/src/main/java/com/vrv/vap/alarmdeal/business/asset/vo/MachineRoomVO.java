package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

@Data
/**
 * 机房VO
 * @author 涂美政
 *
 */
public class MachineRoomVO {
	
    private String guid;
	private String code;
	private Integer width;
	private Integer height;
    private Integer sort;
    private Integer openMonitor;
    private Integer pushMonitor;
    private Integer openAnalysis;
    private Integer pushAnalysis;
	private Integer showWall;
	private String order_;    
	private String by_;   
	private Integer start_;
	private Integer count_;
    
}
