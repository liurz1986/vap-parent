package com.vrv.vap.monitor.server.vo;

import lombok.Data;

@Data
public class DiskInfoVO {
 
	// 磁盘描述
	private String diskName;
	// 磁盘类型
	private String diskType;
	// 逻辑磁盘总容量
	private long diskTotal;
	// 逻辑磁盘总容量
	private String diskTotalCount;
	
	// 逻辑磁盘已使用容量
	private long diskUsed;
	private String diskUsedCount;
	
	// 逻辑磁盘已使用容量
	private long diskFree;
	private String diskFreeCount;
	
	// 逻辑磁盘利用率
	private double diskUsedRate;
	
	// 逻辑磁盘利用率
	private double diskFreeRate;
	
	// 磁盘的单位，如果单位太小，会显示为0 ， 如果磁盘大小为 total1: 198337, free1: 163033, used1:35304 单位为byte， 换算成M为193M， G就为0.18
    //private String utils;

	
}
