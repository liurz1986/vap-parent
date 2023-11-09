package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 上报事件处置文件类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfoDispose {
	/**
	 * 涉密文件数量
	 */
	private Integer file_mm01_count=0;
	/**
	 * 机密文件数量
	 */
	private Integer file_mm02_count=0;
	/**
	 * 绝密文件数量
	 */
	private Integer file_mm03_count=0;
}
