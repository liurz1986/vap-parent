package com.vrv.vap.alarmdeal.business.asset.util;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;


public class AssetUtil {

	public static final String domaimKey="asset_domain_redis_key";   //所有安全域rediskey

	public static final String allAssetTypeGroupKey="asset_type_group_redis_key"; // 所有一级资产类型rediskey

	public static final String allAssetTypeKey="asset_type_redis_key"; // 所有二级资产类型rediskey

	public static final String allHostTypeUnicodeKey="asset_host_type_unicode_redis_key"; // 终端资产类型unicodes的rediskey

	public static final String allPersonKey="asset_person_redis_key"; // 所有人rediskey

	public static final String allAssetSystemAttributeSettingsKey="asset_system_attribute_settings_redis_key";//所有偏好配置redis的可以

	public static final String allAssetSercretLevel="asset_secretlevel_redis_key";//资产涉密等级
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
	
	/**
	 * base64位编码
	 * @param content
	 * @return
	 */
	public static String encoderByBase64(String content,String charSet){
		String result = null;
		Base64 base64 = new Base64();
		try {
			byte[] textByte = content.getBytes(charSet);
			result = base64.encodeToString(textByte);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * base64解码
	 * @param content
	 * @param charSet
	 * @return
	 */
	public static String decoderByBase64(String content,String charSet){
		String result = null;
		Base64 base64 = new Base64();
		try {
			result = new String(base64.decode(content), charSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		  return result;
	}
 
	
	 /**
     * 判断ip是否合法的方式
     * @param str
     * @return
     */
    public static boolean checkIP(String str) {
        Pattern pattern = Pattern
                .compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]"
                        + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
        return pattern.matcher(str).matches();
    }

	/**
	 * 判断URL是否合法的方式
	 * @param str
	 * @return
	 */
	public static boolean checkUrl(String str) {
		String regex = "^((http|ftp|https):\\/\\/)"
				+ "(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})"
				+ "|"
				+ "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))"
				+ "(:([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5]))*"
				+ "(\\/[a-zA-Z0-9\\&#%_\\.\\/-~-]*)*$" ;
		str = str.toLowerCase();
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(str).matches();
	}

}
