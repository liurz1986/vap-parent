package com.vrv.vap.admin.common.util;


import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.util.Base64;

public class EncryptUtil {


    // BASE 65 加密容错时间，这里为30分钟
    private static final int TIME_DIFF = 1000 * 60 * 30;
    // BASE 65 时间长度
    private static final int TIME_LEN = 32;
    // BASE 65 编码加盐（只能为0-10的数字），默认为3
    private static final int SLAT = 3;



    /***
     * MD5加码 生成32位md5码
     */
    public static String encodeMd5(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return str;
        }
        char[] charArray = str.toCharArray();
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


    public static String decodeBase65(String ppp) throws Exception {
        int len = ppp.length();
        if (len % 2 != 0) {
            throw new Exception("Parse Error");
        }
        boolean shortTime = len > TIME_LEN;
        int stop = shortTime ? TIME_LEN : TIME_LEN - (TIME_LEN - len) * 2;
        char[] pb = new char[stop / 2];
        char[] db = new char[stop / 2];
        char[] pa = new char[shortTime ? len - TIME_LEN : 0];
        String da = "";
        try {
            for (int i = 0; i < stop; i = i + 2) {
                pb[i / 2] = (char) (ppp.charAt(i) + SLAT);
                db[i / 2] = ppp.charAt(i + 1);
            }
            if (shortTime) {
                for (int j = TIME_LEN; j < len; j++) {
                    pa[j - TIME_LEN] = (char) (ppp.charAt(j) + SLAT);
                }
            } else {
                da = ppp.substring(stop, len);
            }
            String date = String.valueOf(db) + da;
            String pStr = String.valueOf(pb) + String.valueOf(pa);

            Base64.Decoder decoder = Base64.getDecoder();
            String dateStr = new String(decoder.decode(date));
            String time = "1" + dateStr.substring(0, 11) + "5";
            long dateTime = Long.parseLong(time);
            if (Math.abs(dateTime - System.currentTimeMillis()) > TIME_DIFF) {
                throw new Exception("TimeOut");
            }
            int idx = dateStr.charAt(11) - 65;
            String result = new String(decoder.decode(pStr));
            if (result.length() != idx) {
                throw new Exception("Parse Error");
            }
            return result;
        } catch (Exception error) {
            throw error;
        }
    }
}
