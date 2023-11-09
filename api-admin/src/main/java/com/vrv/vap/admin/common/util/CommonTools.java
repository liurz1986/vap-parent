package com.vrv.vap.admin.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 
 * @author xw
 * @date 2015年11月12日
 */
public final class CommonTools {
	private static final String UTF_8 = "UTF-8";
	private static Log log = LogFactory.getLog(CommonTools.class);

	private static Pattern upPattern = Pattern.compile("[A-Z]");
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
		try {
			return encodeBase64(param.getBytes(UTF_8));
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		return "";
	}

	/**
	 * base64转码
	 * 
	 * @param param
	 * @return
	 */
	public static String encodeBase64(byte[] param) {
		if (null == param) {
			return "";
		}
		return Base64.getEncoder().encodeToString(param);
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
			result = new String(Base64.getDecoder().decode(param), UTF_8);
		} catch (UnsupportedEncodingException e) {
			log.error("",e);
		}
		return result;
	}

	public static boolean isBase64(String str) {
		String base64Pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		return Pattern.matches(base64Pattern, str);
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
	 * 为字符串添加时间 格式 ${name}_yyyyMMdd.SSS
	 * 
	 * @param name
	 * @return
	 */
	public static String appendTime(String name) {
		return new StringBuilder().append(name).append("_").append(TimeTools.formatDate(new Date(), "yyyyMMdd.SSS"))
				.toString();
	}

	/**
	 * 首字符转大写
	 * 
	 * @param tmp
	 * @return
	 */
	public static String upperCaseFirstLetter(String tmp) {
		if ((int) tmp.charAt(0) > 90) {
			char[] cs = tmp.toCharArray();
			cs[0] = (char) (cs[0] - 32);
			return new String(cs);
		}
		return tmp;
	}

	/**
	 * 首字符转小写
	 * 
	 * @param tmp
	 * @return
	 */
	public static String lowerCaseFirstLetter(String tmp) {
		if ((int) tmp.charAt(0) < 91) {
			char[] cs = tmp.toCharArray();
			cs[0] = (char) (cs[0] + 32);
			return new String(cs);
		}
		return tmp;
	}

	/**
	 * 下划线转驼峰
	 * 
	 * @param tmp
	 * @return
	 */
	public static String underLineToCamel(String tmp) {
		String world = "";
		Optional<String> value = Arrays.stream(tmp.split("_")).map(a -> {
			return upperCaseFirstLetter(a);
		}).reduce((a, b) -> a + b);
		if (value.isPresent()) {
			world = value.get();
		}
		return lowerCaseFirstLetter(world);
	}

	/**
	 * 驼峰转下划线
	 *
	 * @param tmp
	 * @return
	 */
	public static String camelToUnderLine(String tmp) {
		Matcher matcher = upPattern.matcher(tmp);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()){
			matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 替换Header中的特殊符号
	 * @return
	 */
	public static String filterHeaderSymbol(String name) {
		if (StringUtils.isNotEmpty(name)) {
			String regex = "[`~!@#$%^&*()\\+\\=\\{}|:\"?><【】\\/r\\/n]";
			Pattern pa = Pattern.compile(regex);
			Matcher ma = pa.matcher(name);
			if(ma.find()){
				name = ma.replaceAll("");
			}
		}
		return name;
	}

	/**
	 * 中文数字
	 */
	private static final String[] CN_NUM = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

	/**
	 * 中文数字单位
	 */
	private static final String[] CN_UNIT = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};

	/**
	 * 特殊字符：负
	 */
	private static final String CN_NEGATIVE = "负";

	/**
	 * 特殊字符：点
	 */
	private static final String CN_POINT = "点";


	/**
	 * int 转 中文数字
	 * 支持到int最大值
	 *
	 * @param intNum 要转换的整型数
	 * @return 中文数字
	 */
	public static String int2chineseNum(int intNum) {
		StringBuffer sb = new StringBuffer();
		boolean isNegative = false;
		if (intNum < 0) {
			isNegative = true;
			intNum *= -1;
		}
		int count = 0;
		while(intNum > 0) {
			sb.insert(0, CN_NUM[intNum % 10] + CN_UNIT[count]);
			intNum = intNum / 10;
			count++;
		}

		if (isNegative)
			sb.insert(0, CN_NEGATIVE);


		return sb.toString().replaceAll("零[千百十]", "零").replaceAll("零+万", "万")
				.replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
				.replaceAll("零+", "零").replaceAll("零$", "").replaceFirst("一十","十");
	}

	/**
	 * bigDecimal 转 中文数字
	 * 整数部分只支持到int的最大值
	 *
	 * @param bigDecimalNum 要转换的BigDecimal数
	 * @return 中文数字
	 */
	public static String bigDecimal2chineseNum(BigDecimal bigDecimalNum) {
		if (bigDecimalNum == null)
			return CN_NUM[0];

		StringBuffer sb = new StringBuffer();

		//将小数点后面的零给去除
		String numStr = bigDecimalNum.abs().stripTrailingZeros().toPlainString();

		String[] split = numStr.split("\\.");
		String integerStr = int2chineseNum(Integer.parseInt(split[0]));

		sb.append(integerStr);

		//如果传入的数有小数，则进行切割，将整数与小数部分分离
		if (split.length == 2) {
			//有小数部分
			sb.append(CN_POINT);
			String decimalStr = split[1];
			char[] chars = decimalStr.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				int index = Integer.parseInt(String.valueOf(chars[i]));
				sb.append(CN_NUM[index]);
			}
		}

		//判断传入数字为正数还是负数
		int signum = bigDecimalNum.signum();
		if (signum == -1) {
			sb.insert(0, CN_NEGATIVE);
		}

		return sb.toString();
	}
}
