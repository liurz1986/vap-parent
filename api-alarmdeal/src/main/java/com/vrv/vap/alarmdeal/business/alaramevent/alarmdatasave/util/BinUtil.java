package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 文件转为二进制
 */
public class BinUtil {

  private static  Logger logger= LoggerFactory.getLogger(BinUtil.class);

	/**
	 * 文件转为二进制字符串
	 *
	 * @param filePath 文件所在的路径
	 * @return
	 */
	public static String fileToBinStr(String filePath) {
		try {
			InputStream fis = new FileInputStream(filePath);
			byte[] bytes = FileCopyUtils.copyToByteArray(fis);
			return new String(bytes, "ISO-8859-1");
		} catch (Exception ex) {
			logger.error("transform file into bin String 出错,{}",ex);
			return "";
		}
	}


	/**
	 * 二进制字符串转文件
	 *
	 * @param bin      二进制字符串
	 * @param filePath 文件保存路径
	 * @return
	 */
	public static File binStrToFile(String bin, String filePath) {
		try {
			File fout = new File(filePath);
			fout.createNewFile();
			byte[] bytes1 = bin.getBytes("ISO-8859-1");
			FileCopyUtils.copy(bytes1, fout);
			return fout;
		} catch (Exception ex) {
			logger.error("transform bin into File 出错,出错原因为={}",ex);
			return null;
		}
	}


}


