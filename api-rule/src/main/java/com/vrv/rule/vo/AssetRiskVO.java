package com.vrv.rule.vo;

import java.util.Date;

import lombok.Data;

@Data
public class AssetRiskVO {
     
	private String ip;
	private Long num;
	private Integer weight;
	private Date startTime; //开始时间
	private Date endTime; //结束时间
	private String assetguids; //资产guids
	
}
