package com.flink.demo.vo; 
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月24日 上午11:01:28 
* 类说明 
*/

import java.sql.Timestamp;

import lombok.Data;

@Data
public class FlinkSqlVO {
    
	private String user; //用户
	private Long cnt; //url
    private Timestamp trigger_time; //触发时间 
}
