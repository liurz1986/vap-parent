package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.Map;

import lombok.Data;

/**
 * soarvo
 * @author wd-pc
 *
 */
@Data
public class SoarVO {

	private String scriptCode; //剧本Code
	private Map<String,Object> scriptParams; //入参 
	
	
}
