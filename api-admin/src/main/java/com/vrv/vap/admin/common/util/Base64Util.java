package com.vrv.vap.admin.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;


public class Base64Util {

    private static Logger logger = LoggerFactory.getLogger(Base64Util.class);

    /**
     * 16进制解析
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || "".equals(s)) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                logger.error("",e);
            }
        }
        try {
            s = new String(baKeyword, "utf-8");
            new String();
        } catch (Exception e1) {
            logger.error("",e1);
        }
        return s;
    }
    public static String decoderByBase64(String content, String charSet) {
        String result = null;
        Base64 base64 = new Base64();
        try {
            result = new String(base64.decode(content), charSet);
        } catch (UnsupportedEncodingException e) {
            logger.error("",e);
        }
        return result;
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
            return encodeBase64(param.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
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
        return java.util.Base64.getEncoder().encodeToString(param);
    }

    //base64字符串转byte[]
    public static byte[] base64String2ByteFun(String base64Str){
        return Base64.decodeBase64(base64Str);
    }

}
