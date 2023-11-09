package com.vrv.vap.alarmdeal.frameworks.contract;

import lombok.Data;
@Data	
public class FileInfo {


	private static final long serialVersionUID = 1L;

	private String guid;

	private String namespace;
	private String bucketname;  // 文件系统中存放基础路径，oss中存放bucket。

	private String filePath; // 文件路径， 存放相对路径。 = namespace的转换路径
	private String fileName; // 文件名称

	private String uploadType; // 上传标识  oss表示oss上传type  local表示本地上传方式

	private String fileType;  // 文件类型doc, xls

	private int override;  // 0 表示覆盖， 1表示不覆盖

	private long createTime; // 创建时间

	private String userName; // 用户名称
	
	private String userId;   // 用户id

}
