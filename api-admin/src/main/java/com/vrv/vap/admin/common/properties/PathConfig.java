package com.vrv.vap.admin.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件路径配置
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
@Component
@ConfigurationProperties(prefix = "dir")
public class PathConfig {

	private String base;
	private String upload;
	private String tmp;

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getUpload() {
		return upload;
	}

	public void setUpload(String upload) {
		this.upload = upload;
	}

	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}
}
