package com.vrv.vap.monitor.agent.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * 文件夹工具
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	/**
	 * 删除单个文件
	 *
	 * @param filePath：要删除的文件的全路径
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String filePath) {
		File file = new File(filePath);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				logger.info("删除单个文件" + filePath + "成功！");
				return true;
			} else {
				logger.info("删除单个文件" + filePath + "失败！");
				return false;
			}
		} else {
			logger.info("删除单个文件失败：" + filePath + "不存在！");
			return false;
		}
	}

	/**
	 * 删除目录及目录下的文件
	 *
	 * @param dir：要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			logger.info("删除目录失败：" + dir + "不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			logger.info("删除目录失败！");
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			logger.info("删除目录" + dir + "成功！");
			return true;
		} else {
			return false;
		}
	}

	public static boolean uploadFile(MultipartFile file, String filePath) {
		try {
            File dest = new File(filePath);
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
			return true;
		} catch (Exception e) {
			logger.error("文件上传失败" , e);
		}
		return false;
	}

	public static boolean downloadFile(String filePath, HttpServletResponse response) {
		boolean result = false;
		File file = new File(filePath);
		if (!file.exists()) {
			logger.error("文件不存在");
			return result;
		}
		try (InputStream fis = new BufferedInputStream(new FileInputStream(file));
			 OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
			// 取得文件名
			String filename = file.getName();

			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
			response.setContentType("application/octet-stream");
			out.write(buffer);
			out.flush();
			result = true;
		} catch (IOException e) {
			logger.info("文件下载失败", e);
		}
		return result;
	}

	public static boolean createFile(String filePath){
		File file = new File(filePath);
		File fileParent = file.getParentFile();
		if(!fileParent.exists()){
			fileParent.mkdirs();
		}
		try {
			file.createNewFile();
			return true;
		} catch (IOException e) {
			logger.error(e+"");
		}
		return false;
	}

	public static boolean writeFile( List<String> contents,String filePath)
	{
		Writer w = null;
		try
		{
			w = new FileWriter(filePath);
			for(String content:contents){
				w.write(content);
				w.write(System.getProperty("line.separator"));
			}
			w.close();
		} catch (IOException e) {
			logger.error(e+"");
			return false;
		} finally {
			try {
				if (w != null) {
					w.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static boolean writeFile( String content,String filePath)
	{
		Writer w = null;
		try
		{
			w = new FileWriter(CleanUtil.cleanString(filePath));
			w.write(content);
			w.close();
		} catch (IOException e) {
			logger.error(e+"");
			return false;
		} finally {
			try {
				if (w != null) {
					w.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static void reNameFile(String oldFilePath,String newFilePath){
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);
		oldFile.renameTo(newFile);
	}

	/**
	 * 读取对应文件的内容(以行作为分割符进行操作)
	 * @param filenPath
	 * @return
	 */
	public static List<String> readFile(String filenPath) {
		List<String> list = new ArrayList<>();
		// 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
		//防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
		//不关闭文件会导致资源的泄露，读写文件都同理
		//Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
		try (FileReader reader = new FileReader(filenPath);
			 BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
		) {
			String line= null;
			//网友推荐更加简洁的写法
			while ((line = br.readLine()) != null) {
				// 一次读入一行数据
				list.add(line);
				//content.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}


}
