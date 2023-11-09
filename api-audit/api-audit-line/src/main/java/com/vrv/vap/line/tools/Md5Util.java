package com.vrv.vap.line.tools;

import org.apache.commons.codec.binary.Hex;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 采用MD5加密解密
 */
public class Md5Util {

    /***
     * MD5加码 生成32位md5码
     */
    public static String string2Md5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }
    /**
     * 根据自己的规则进行MD5加密
     * 例如，现在需求是有字符串传入zhang，xy
     * 其中zhang是传入的字符
     * 然后需要将zhang的字符进行拆分z，和hang，
     * 最后需要加密的字段为zxyhang
     */
    public static String md5Test(String inStr){
        String xy = "xy";
        String finalStr="";
        if(inStr!=null){
            String fStr = inStr.substring(0, 1);
            String lStr = inStr.substring(1, inStr.length());
            finalStr = string2Md5( fStr+xy+lStr);

        }else{
            finalStr = string2Md5(xy);
        }
        return finalStr;
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     * @return md5 value
     */
    public static String getMD5(MultipartFile multipartFile) {
        InputStream inputStream= null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            inputStream = multipartFile.getInputStream();
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
