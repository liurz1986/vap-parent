package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import lombok.Data;
/**
 * 依赖资源
 * @author wd-pc
 *
 */
@Data
public class Dependencies {

	private String guid;  //实际上是code
	private String type;
	private String version;
	private String name;
}
