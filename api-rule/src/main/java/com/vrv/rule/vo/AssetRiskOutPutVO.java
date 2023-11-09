package com.vrv.rule.vo; 
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月30日 下午2:50:27 
* 类说明     输出类
*/

import java.sql.Timestamp;

import lombok.Data;

/**
 * 资产风险输出日志
 * @author wd-pc
 *
 */
@Data
public class AssetRiskOutPutVO {
      
	private String ip;
	private Long num;
	private Integer weight;
	private Timestamp startTime; //开始时间
	//private Timestamp endTime; //结束时间
	//private String assetguids; //资产guids
	
}
