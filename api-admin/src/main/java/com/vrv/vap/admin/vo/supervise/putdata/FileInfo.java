package com.vrv.vap.admin.vo.supervise.putdata;

import com.vrv.vap.admin.config.PutField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileInfo {
	// 文件名称
	@PutField("file_name")
	@ApiModelProperty(value = "文件名称")
	String fileName;
	// 密级
	@PutField("file_security_level")
	@ApiModelProperty(value = "密级")
	String fileSecurityLevel;
	// 文件md5
	@PutField("file_md5")
	@ApiModelProperty(value = "文件md5")
	String fileMd5;
	// 文件类型
	@PutField("file_type")
	@ApiModelProperty(value = "文件类型")
	String fileType;
	// 文件存储路径
	@PutField("file_storage_path")
	@ApiModelProperty(value = "文件存储路径")
	String fileStoragePath;
	// 文件业务范围
	@PutField("file_business")
	@ApiModelProperty(value = "文件业务范围")
	String fileBusiness;
	// 数据流向位置
	@PutField("file_data_to_address")
	@ApiModelProperty(value = "数据流向位置")
	String fileDataToAddress;
	// 数据下载或爬取位置
	@PutField("file_data_download_address")
	@ApiModelProperty(value = "数据下载或爬取位置")
	String fileDataDownloadAddress;
	
	
	
}
