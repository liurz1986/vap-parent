package com.flink.demo.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;

@Data
public class SwitchFlagVO implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String switchId; //交换机Id
	private Integer portCount; //端口数量
	private String portId; //端口Id
	private Boolean exception; //是否异常
	private Integer speedValue; //流量值
	private Timestamp happenTime; //发生时间
	private Integer exceptionCount;
	private String exceptionInfo;
	private Integer accumulateCount;
	private String flag;
	
}
