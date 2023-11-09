package com.vrv.vap.admin.vo;

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
