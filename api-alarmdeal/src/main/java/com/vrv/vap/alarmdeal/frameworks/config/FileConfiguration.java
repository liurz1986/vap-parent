package com.vrv.vap.alarmdeal.frameworks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
/**
 * FileConfiguration
 * @author wd-pc
 *
 */

@Configuration
@Data
@ConfigurationProperties(prefix="file")
public class FileConfiguration {

	private String filePath; //文件路径
	private String fileName; //文件名称
	private String threatLibraryName; //威胁库文件名称
	private String threatInfoName; //威胁管理名称

	// cvs文件导出配置 --合并加入 2021-09-23
	private String cvsFilePath; //CVS文件路径
	private String cvsFileDevName; //CVS文件名称
	private String cvsFileServerName; //CVS文件名称
    // 流程 2021-10-14
	private String filePathFlow; //文件路径
	private String fileNameFlow; //文件名称
	private String zipNameFlow; //文件压缩名称

	// 模型管理 2022-04-22
	private String tempModelPath; //模型上传临时存放点
	private String runModelPath; //模型存放位置

	// 资产在线
	private String assetOnLinePath; //资产在线文件下载
	// 资产
	private String asset; //资产文件下载
}
