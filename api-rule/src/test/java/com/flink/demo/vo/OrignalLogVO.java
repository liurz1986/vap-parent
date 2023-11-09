package com.flink.demo.vo;

import java.sql.Timestamp;

import lombok.Data;

/**
 * * 
 * 
 * @author wudi   E‐mail:wudi@vrvmail.com.cn
 * @version 创建时间：2018年12月20日 上午10:03:11  
 * 类说明  原始日志VO
 */
@Data
public class OrignalLogVO {
	private Timestamp trigger_time; //触发时间
    private String related_ips; //关联IP
    private String dst_ips; //目的IP
    private String dst_ports; //目的端口
    private String src_ips; //源IP
    private String src_ports; //源端口
    
    
    
}
