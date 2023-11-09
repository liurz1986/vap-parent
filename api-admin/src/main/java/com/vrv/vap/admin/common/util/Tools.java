package com.vrv.vap.admin.common.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.*;

/**
 * 工具类
 * 
 * @author xw
 * @date 2015年11月12日
 */
@Component
public final class Tools {

	public static final String UTF_8 = "UTF-8";
	private static Logger log = LoggerFactory.getLogger(Tools.class);
	@Value("${des.key}")
	public  String DES_KEY;
	
	private static Tools tools;
	
	@PostConstruct 
	public void init() {
		tools = this;
		tools.DES_KEY = DES_KEY;
	}

	/**
	 * 获取主键id
	 * 
	 * @return
	 */
	public static String generateId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/***
	 * MD5加码 生成32位md5码
	 */
	public static String string2MD5(String inStr) {
		if (StringUtils.isEmpty(inStr)) {
			return inStr;
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			log.error("", e);
			return inStr;
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * base64转码
	 * 
	 * @param param
	 * @return
	 */
	public static String encodeBase64(String param) {
		if (StringUtils.isEmpty(param)) {
			return "";
		}
		String result = "";
		try {
			result = new Base64().encodeAsString(param.getBytes(UTF_8));
		} catch (UnsupportedEncodingException e) {
			log.error("",e);
		}
		return result;
	}

	/**
	 * base64解码
	 *
	 * @param param
	 * @return
	 */
	public static String decodeBase64(String param) {
		if (StringUtils.isEmpty(param)) {
			return "";
		}
		String result = "";
		try {
			result = new String(new Base64().decode(param), UTF_8);
		} catch (UnsupportedEncodingException e) {
			log.error("",e);
		}
		return result;
	}

	/**
	 * base64解码
	 *
	 * @param params
	 * @return
	 */
	public static String[] decodeBase64(String[] params) {
		List<String> result = new ArrayList<String>();
		try {
			for (String param : params) {
				result.add(decodeBase64(param));
			}
		} catch (Exception e) {
			log.error("",e);
		}
		return getStringArray(result);
	}

	public static String[] getStringArray(List<String> strings) {
		return strings.toArray(new String[strings.size()]);
	}

	/**
	 * ip按第三级分类
	 * 
	 * @param ips
	 * @return
	 */
	public static Map<String, List<String>> ipSort(List<String> ips) {
		Map<String, List<String>> ip_c = new HashMap<String, List<String>>();
		StringBuffer sb = new StringBuffer();
		List<String> list = null;
		for (int i = 0; i < ips.size(); i++) {
			String[] ip_s = split(ips.get(i), ".");
			if (4 != ip_s.length) {
				continue;
			}
			sb.append(ip_s[0]).append(".").append(ip_s[1]).append(".").append(ip_s[2]);
			if (ip_c.containsKey(sb.toString())) {
				ip_c.get(sb.toString()).add(ip_s[3]);
			} else {
				list = new ArrayList<String>();
				list.add(ip_s[3]);
				ip_c.put(sb.toString(), list);
			}
			sb.setLength(0);
		}

		return ip_c;
	}

	public static String[] split(String src, String split) {
		List<String> list = new ArrayList<String>();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < src.length(); i++) {
			if (split.charAt(0) == src.charAt(i)) {
				list.add(sb.toString());
				sb.setLength(0);
				continue;
			}
			sb.append(src.charAt(i));
		}
		list.add(sb.toString());
		sb.setLength(0);

		String[] results = new String[list.size()];
		System.arraycopy(list.toArray(), 0, results, 0, results.length);

		return results;
	}

	/**
	 * ip全段分类
	 * 
	 * @param ips
	 * @return
	 */
	public static Map<String, Map<String, Map<String, Set<String>>>> ipSorts(List<String> ips) {
		Map<String, Map<String, Map<String, Set<String>>>> result = new HashMap<String, Map<String, Map<String, Set<String>>>>();

		for (String ip : ips) {
			String[] ip4 = split(ip, ".");
			if (ip4.length != 4) {
				continue;
			}
			if (result.containsKey(ip4[0])) {
				if (result.get(ip4[0]).containsKey(ip4[1])) {
					if (result.get(ip4[0]).get(ip4[1]).containsKey(ip4[2])) {
						result.get(ip4[0]).get(ip4[1]).get(ip4[2]).add(ip4[3]);
					} else {
						Set<String> sets = new HashSet<String>();
						sets.add(ip4[3]);
						result.get(ip4[0]).get(ip4[1]).put(ip4[2], sets);
					}
				} else {
					Map<String, Set<String>> map = new HashMap<String, Set<String>>();
					Set<String> sets = new HashSet<String>();
					sets.add(ip4[3]);
					map.put(ip4[2], sets);
					result.get(ip4[0]).put(ip4[1], map);
				}
			} else {
				Map<String, Map<String, Set<String>>> mapmap = new HashMap<String, Map<String, Set<String>>>();
				Map<String, Set<String>> map = new HashMap<String, Set<String>>();
				Set<String> sets = new HashSet<String>();
				sets.add(ip4[3]);
				map.put(ip4[2], sets);
				mapmap.put(ip4[1], map);
				result.put(ip4[0], mapmap);
			}
		}

		return result;
	}
}
