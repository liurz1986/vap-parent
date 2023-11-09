package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月17日 下午2:09:52 
* 类说明 
*/
@Data
public class AssetPathVO {
      
	private AssetIpVO srcAsset;  //源资产
	private AssetIpVO dstAsset; //目的资产
	private Long count; //个数
	
}
