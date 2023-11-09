package com.flink.demo.vo; 
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月24日 上午11:28:35 
* 类说明 
*/

import java.sql.Timestamp;

import lombok.Data;

/**
 * sql原始日志
 * @author wd-pc
 *
 */
@Data
public class FlinkLogVO {
     
	private String user;
	private String url;
	private Timestamp trigger_time;
}
