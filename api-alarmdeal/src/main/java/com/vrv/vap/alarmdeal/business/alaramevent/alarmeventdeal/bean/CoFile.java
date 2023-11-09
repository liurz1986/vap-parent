package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 文件信息，包含文件名称和文件二进制内容
 */
@Data
public class CoFile {
	//fileName或者file_name都可以被接收
	@SerializedName(value = "file_name",alternate = {"fileName"})
	private String file_name;
	//filePath和file_bin都可以接收
	@SerializedName(value = "file_bin",alternate = {"filePath"})
	private String file_bin;
}
