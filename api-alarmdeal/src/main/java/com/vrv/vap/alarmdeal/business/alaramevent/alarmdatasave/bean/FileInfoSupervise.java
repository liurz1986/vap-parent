package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 文件信息映射类
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileInfoSupervise {
	/**
	 * 文件名称
	 */
//	@SerializedName(value = "file_name",alternate = {"fileName"})
//	private String file_name;
	/**
	 * 密级
	 */
	@SerializedName(value = "file_security_level",alternate = {"fileSecurityLevel"})
	private String file_security_level;
	/**
	 * 文件md5
	 */
	@SerializedName(value = "file_md5",alternate = {"fileMd5"})
	private String file_md5;
	/**
	 * 文件类型
	 */
//	@SerializedName(value = "file_type",alternate = {"fileType"})
//	private String file_type;
	/**
	 * 文件存储路径
	 */
//	@SerializedName(value = "file_storage_path",alternate = {"fileStoragePath"})
//	private String file_storage_path;
	/**
	 * 文件业务范围
	 */
//	@SerializedName(value = "file_business",alternate = {"fileBusiness"})
//	private String file_business;
//	/**
//	 * 数据流向位置
//	 */
//	@SerializedName(value = "file_data_to_address",alternate = {"fileDataToAddress"})
//	private String file_data_to_address;
//	/**
//	 * 数据下载或爬取位置
//	 */
//	@SerializedName(value = "file_data_download_address",alternate = {"fileDataDownloadAddress"})
//	private String file_data_download_address;
}
