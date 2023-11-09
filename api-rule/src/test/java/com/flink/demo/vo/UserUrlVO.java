package com.flink.demo.vo; 
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年7月3日 下午4:49:57 
* 类说明 
*/

import java.sql.Timestamp;

import lombok.Data;

@Data
public class UserUrlVO {

	private String userName;
	private String url;
	private long cnt;
	
}
