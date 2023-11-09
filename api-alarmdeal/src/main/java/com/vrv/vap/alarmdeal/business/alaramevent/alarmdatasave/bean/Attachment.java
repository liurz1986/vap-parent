package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 附件对象
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment {
	//文件名称
	private String file_name;
	//文件路径
	private String file_path;
	//文件二进制内容
	private String file_bin;
}
