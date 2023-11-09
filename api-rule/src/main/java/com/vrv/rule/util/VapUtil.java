package com.vrv.rule.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年8月7日 下午2:05:26 
* 类说明 
*/
public class VapUtil {

	/**
	 * ip转成数值
	 * @param ip
	 * @return
	 */
	public static Long ip2int(String ip) {
		Long num = 0L;
		if (ip == null) {
			return num;
		}

		try {
			ip = ip.replaceAll("[^0-9\\.]", ""); // 去除字符串前的空字符
			String[] ips = ip.split("\\.");
			if (ips.length == 4) {
				num = Long.parseLong(ips[0], 10) * 256L * 256L * 256L + Long.parseLong(ips[1], 10) * 256L * 256L + Long.parseLong(ips[2], 10) * 256L + Long.parseLong(ips[3], 10);
				num = num >>> 0;
			}
		} catch (NullPointerException ex) {
			System.out.println(ip);
		}

		return num;
	}


	//Long转换为IP
	public static String number2Ip(Long number) {
		if(number == null){
			return null;
		}
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((number >>> 24)));
		sb.append(".");
		sb.append(String.valueOf((number & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((number & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((number & 0x000000FF)));
		return sb.toString();
	}
	
	
	public static boolean getResourceResultBySign(String sign) {
		if(sign.equals("contain")) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 获得解码以后的正则表达式
	 * @param content
	 * @return
	 */
	public static List<String> getContentList(List<String> contents){
		List<String> list = new ArrayList<>();
		for (String content : contents) {
			try {
				String decode = URLDecoder.decode(content, "utf-8");
				list.add(decode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	public static void main(String[] args) {
		Long ip2int = ip2int("192.168.0.0");
		String number2Ip = number2Ip(ip2int);
		System.out.println(ip2int);
		System.out.println(number2Ip);
	}
	
}
