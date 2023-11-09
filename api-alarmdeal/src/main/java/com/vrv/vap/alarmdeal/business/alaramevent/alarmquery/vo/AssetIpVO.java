package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import com.vrv.vap.alarmdeal.business.asset.model.Asset;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月17日 上午10:54:24 
* 类说明 
*/
@Data
public class AssetIpVO {

	private String ip;
	private Asset asset;
	private Long count;
}
