package com.vrv.vap.admin.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LocalHostInfoVO {
 
	
	private String ip; 
	// 主机名称
	private String systemName;

	private String versionInfo;
	// 设备运行时间
	private String runningTime;
	
	//服务器系统时间
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date systemTime;
	
	//服务器启动时间
	private String bootTime;
	
	// cpu信息
	private CpuInfoVO cpuInfoVo;
	// 内存信息
	private RamInfoVO ramInfoVo;
	// 磁盘信息
	private List<DiskInfoVO> diskInfoVo;
	// 备注信息
	private String note;
	
}
