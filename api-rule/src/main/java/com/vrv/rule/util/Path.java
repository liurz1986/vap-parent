package com.vrv.rule.util;

import java.io.File;

public class Path {
	
//	/**
//	 * 拼接路径，多参数
//	 * @param path1
//	 * @param path2
//	 * @param paths
//	 * @return
//	 */
//	public static String combine(String path1, String path2, String ... paths)
//	{
//		return "";
//	}
	
	/**
	 * 按路径规则，进行路径拼接
	 * @param path1 拼接在前面的
	 * @param path2 拼接在后面的
	 * @return
	 */
	public static String combine(String path1, String path2)
	{
		if(!path1.endsWith(File.separator))
		{
			path1 = path1+File.separator;
		}
		if(path2.startsWith(File.separator))
		{
			path2 = path2.substring(1);
		}
		return path1 + path2;
	}
	
	/**
	 * 按路径规则，进行路径拼接
	 * @param path1 拼接在前面的
	 * @param path2 拼接在后面的
	 * @return
	 */
	public static String combine(String path1, String path2, String separator)
	{
		if(!path1.endsWith(separator))
		{
			path1 = path1+separator;
		}
		if(path2.startsWith(separator))
		{
			path2 = path2.substring(1);
		}
		return path1 + path2;
	}
	
	public static String getDirectoryName(String path)
	{
		return "";
	}
	
	public static String getExtension(String path)
	{
		return "";
	}
	
	public static String getFileName(String path)
	{
		return "";
	}
	
	public static String getFileNameWithoutExtension(String path)
	{
		String fileName = "";
		int lastIndexOf = -1;
		if(path.startsWith("/"))
		{
			lastIndexOf = path.lastIndexOf("/");
		}
		else
		{
			lastIndexOf = path.lastIndexOf(File.separator);
		}
		if(lastIndexOf>0)
		{
			fileName = path.substring(lastIndexOf + 1);
		}
		
		return fileName;
	}
}
