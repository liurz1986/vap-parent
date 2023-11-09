package com.vrv.vap.monitor.server.vo;

import lombok.Data;

@Data
public class CpuInfoVO {

	// 设备平均CPU利用率（纵坐标）
	private String usedRate;

	// CPU的核数
	private int physicalProcessorCount;
	
	// CPU的核数
	private int logicalProcessorCount;

	// 进程数
	private int processorCount;
	
	// 线程数
	private int threadCount;
}
