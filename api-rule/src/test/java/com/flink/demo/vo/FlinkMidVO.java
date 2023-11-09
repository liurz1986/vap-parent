package com.flink.demo.vo;

import java.sql.Timestamp;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月24日 下午2:01:11 
* 类说明     flink
*/
@Data
public class FlinkMidVO {
    
	private String user; //用户
	private Long cnt; //个数
	private Timestamp trigger_time; //触发时间  
	private FlinkLogVO flinkLogVO;
}
