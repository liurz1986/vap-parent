package com.flink.demo.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
@Data
public class SwitchFoldVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer accumulateCount;
	private Integer exceptionCount;
	private String exceptionInfo;
	private String switchId; //交换机Id
	private Integer portCount; //端口数量
	private String portId; //端口Id
	private Boolean exception; //是否异常
	private Integer speedValue; //流量值
	private Timestamp time; //发生时间
	
	
    
	
	
}
