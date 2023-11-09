package com.vrv.vap.admin.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.regex.Pattern;

/**
 * 
 * 文件上传工具类
 * 
 * @author xw
 * @date 2015年11月11日
 */
@Component
public final class UploadTools
{
	private static Logger log = LoggerFactory.getLogger(UploadTools.class);
	@Value("${upload}")
	private String UPLOAD;
	
	private static UploadTools uploadTools;
	
	@PostConstruct 
	public void init() {
		uploadTools = this;
		uploadTools.UPLOAD = UPLOAD;
	}
	
	/**
	 * 获取sys.properties配置的上传文件保存路径
	 * @param request
	 * @return
	 */
	public static String getUploadDirPath(HttpServletRequest request)
	{
		return request.getSession().getServletContext()
				.getRealPath(uploadTools.UPLOAD);
	}

	/**
	 * 根据前缀后缀添加时间戳
	 * @param prefix
	 * @param postfix
	 * @return
	 */
	public static String createNewFileName(String prefix, String postfix)
	{
		return prefix + TimeTools.formatTimeStamp(TimeTools.getNow()) + postfix;
	}


	// 保存文件
	public static boolean writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			return true;
		} catch (IOException e) {
            e.printStackTrace();
		}
		return false;
	}


	/**
	 * 判断上传的文件是否是excel文件
	 * @param fileType
	 * @return
	 */
	public static boolean isExcel(String fileType)
	{
		return Pattern.matches("application/.*(excel|sheet)$", fileType);
	}
}
