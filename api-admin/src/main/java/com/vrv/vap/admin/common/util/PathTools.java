package com.vrv.vap.admin.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 文件夹工具
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
@Component
public final class PathTools {


	@Value("${dir.base}")
	private String base;
	@Value("${dir.upload}")
	private String upload;
	@Value("${dir.tmp}")
	private String tmp;

	private static PathTools pathTools;

	@PostConstruct
	public void init() {
		pathTools = this;
		pathTools.base = base;
		pathTools.upload = upload;
		pathTools.tmp = tmp;
		setPathConfig();
	}
	/**
	 * 服务启动时设置一次
	 *
	 * @param
	 */
	public static void setPathConfig() {

		createDir(getBaseDir());
		createDir(getUploadDir());
		createDir(getTemporaryDir());
		createDir(getExcelSavePath());
	}

	private static void createDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			log.info("创建目录: " + path);
			file.mkdirs();
		}
	}

	/**
	 * 获取工程根目录
	 *
	 * @return
	 */
	public static String getBaseDir() {
		return pathTools.base;
	}

	/**
	 * 获取上传文件保存目录
	 *
	 * @return
	 */
	public static String getUploadDir() {
		return getBaseDir() + pathTools.upload;
	}

	/**
	 * 获取临时文件保存目录
	 *
	 * @return
	 */
	public static String getTemporaryDir() {
		return getBaseDir() + pathTools.tmp;
	}

	/**
	 * 获取文件保存地址
	 * 
	 * @return
	 */
	public static String getExcelPath(String filename) {
		return new StringBuilder().append(getExcelSavePath()).append(CommonTools.appendTime(filename)).append(".xls")
				.toString();
	}

	/**
	 * 获取文件保存地址
	 * 
	 * @return
	 */
	public static String getExcelSavePath() {
		return new StringBuilder().append(getTemporaryDir()).append("/excel/").toString();
	}

	private static Log log = LogFactory.getLog(PathTools.class);

	/**
	 * 获取项目路径
	 * 
	 * @return
	 */
	public static String getBasePath() {
		String basePath = PathTools.class.getResource("/").getPath();
		return basePath;
	}

	/**
	 * 获取文件输入流
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getInputStrem(String fileName) {
		InputStream in = null;
		try {
			log.info("get " + fileName + " from [" + getBasePath() + "]");
			in = new FileInputStream(new File(getBasePath() + fileName));
		} catch (FileNotFoundException e) {
			log.error("", e);
		}
		return in;
	}

	// public static void main(String[] args) {
	// System.out.println(DirTools.getProjectDir());
	// }
}
