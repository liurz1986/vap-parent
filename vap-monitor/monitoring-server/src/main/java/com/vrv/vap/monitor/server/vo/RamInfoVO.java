package com.vrv.vap.monitor.server.vo;

import lombok.Data;

@Data
public class RamInfoVO {
 
	// 内存大小
	private long ramSize;
	// 已用内存
	private long usedRam;
	// 已用百分比
	private double percentAge;
 
}
