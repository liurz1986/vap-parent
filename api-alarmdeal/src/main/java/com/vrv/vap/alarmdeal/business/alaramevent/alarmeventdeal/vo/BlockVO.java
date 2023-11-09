package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.Map;

import lombok.Data;

/**
 * 阻断接口实体
 * @author wd-pc
 *
 */
@Data
public class BlockVO {

	private String guid; //设备规则guid
	private String assetIp; //设备IP
	private String blockIp; //阻断IP
	private Map<String,Object> param;//设备参数
	
	
	
}
