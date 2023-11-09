package com.vrv.vap.admin.common.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月26日 上午10:34:57 
* 类说明     字符操作说明
*/
public class StringUtil {

	
	/**
	 * 返回实际的长度
	 * @param data
	 * @return
	 */
	 public static int returnActualLength(byte[] data) { 
	        int i = 0; 
	        for (; i < data.length; i++) { 
	            if (data[i] == '\0') {
	                break; 
	            }
	        } 
	        return i; 
	    }
	
	 /**
	  * byte字符串解析操作
	  * @param bytes
	  * @param charsetName
	  * @return
	  */
	public static String getString(byte[] bytes, String charsetName) {
		int returnActualLength = returnActualLength(bytes);
		return new String(bytes,0,returnActualLength, Charset.forName(charsetName));
	}
	
	/**
	 * 根据GBK格式进行解析
	 * @param barray
	 * @return
	 */
	public static String getString(byte[] barray) {
		return getString(barray, "GBK");
	}

	
	/**
	 * 二分法数组的倒序
	 * @param bytes
	 * @return
	 */
	public static byte[] toRerverArr(byte[] bytes) {
		for (int i = 0; i < bytes.length/2; i++) {
			byte temp = bytes[i];
			bytes[i] = bytes[bytes.length - 1 - i];
			bytes[bytes.length - 1 - i] = temp;
		}
		return bytes;
	}
	
	
	/**
	 * byte[]数组转换成为对应的进制数（2,8,10,16进制）
	 * @param bytes
	 * @param radix
	 * @return
	 */
	public static String binary(byte[] bytes, int radix) {
		return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
	}
    
	/**
	 * 更新截止时间
	 * @param deadLine
	 * @return
	 */
	public static String getDeadLineExpress(String deadLine) {
		String year = deadLine.substring(0, 4);
		List<String> timeFlag = getTimeFlag(deadLine);
		String mouth = timeFlag.get(0);
		String day = timeFlag.get(1);
		String triggerTime = "0 0 1 " + day + " " + mouth + " "+"?"+" "+ year; //截止时间日期
		System.out.println(triggerTime);
		return triggerTime;
	}
	
	/**
	 * 切分时间的标识符（授权专用限定时间格式yyyyMMdd）
	 * @param deadLine
	 * @return
	 */
	public static List<String> getTimeFlag(String deadLine){
		List<String> list = new ArrayList<String>();
		String mouth = null;
		String day = null;
		String mouthFlag = deadLine .substring(4, 5);
		String dayFlag = deadLine.substring(6,7);
		if(Integer.valueOf(mouthFlag)!=0){
			mouth = deadLine.substring(4, 6);
		}else{
			mouth = deadLine.substring(5, 6);
		}
		if(Integer.valueOf(dayFlag)!=0){
			day = deadLine.substring(6, deadLine.length());
		}else{
			day = deadLine.substring(7, deadLine.length());
		}
		list.add(mouth);
		list.add(day);
		return list;
	}

	public static String getRandomStr(int bytes){
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < bytes; i++) {
			//随机判断判断该字符是数字还是字母
			String choice = random.nextInt(2) % 2 == 0 ? "char" : "num";
			if ("char".equalsIgnoreCase(choice)) {
				//随机判断是大写字母还是小写字母
				int start = random.nextInt(2) % 2 == 0 ? 65 : 97;
				sb.append((char) (start + random.nextInt(26)));
			} else if ("num".equalsIgnoreCase(choice)) {
				sb.append(random.nextInt(10));
			}
		}
		return sb.toString();
	}
	
}
