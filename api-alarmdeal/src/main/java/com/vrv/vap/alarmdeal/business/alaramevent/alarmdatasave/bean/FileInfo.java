package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.vrv.vap.alarmdeal.frameworks.config.EsField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileInfo {
	// 文件名称
	@EsField("file_name")
	@ApiModelProperty(value = "文件名称")
	String fileName;
	// 密级
	@EsField("file_security_level")
	@ApiModelProperty(value = "密级")
	String fileSecurityLevel;
	// 文件md5
	@EsField("file_md5")
	@ApiModelProperty(value = "文件md5")
	String fileMd5;
	// 文件类型
	@EsField("file_type")
	@ApiModelProperty(value = "文件类型")
	String fileType;
	// 文件存储路径
	@EsField("file_storage_path")
	@ApiModelProperty(value = "文件存储路径")
	String fileStoragePath;
	// 文件业务范围
	@EsField("file_business")
	@ApiModelProperty(value = "文件业务范围")
	String fileBusiness;
	// 数据流向位置
	@EsField("file_data_to_address")
	@ApiModelProperty(value = "数据流向位置")
	String fileDataToAddress;
	// 数据下载或爬取位置
	@EsField("file_data_download_address")
	@ApiModelProperty(value = "数据下载或爬取位置")
	String fileDataDownloadAddress;
	
	
	
}
