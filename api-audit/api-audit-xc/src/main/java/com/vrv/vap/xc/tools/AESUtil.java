package com.vrv.vap.xc.tools;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author lilang
 * @date 2021/4/6
 * @description
 */
public class AESUtil {
    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // "算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        // 此处使用BASE64做转码功能，同时能起到2次加密的作用。
        return new BASE64Encoder().encodeBuffer(encrypted);
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            // 先用base64解密
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        //EncryptUtil.

        /*String s = "123*<>$@!--%哈哈";
        String en = new BASE64Encoder().encode(s.getBytes(StandardCharsets.UTF_8));
        System.out.println("加密后："+en);*/
        String s = "ewoJCSJuYW1lQ29sIjoib3JnX25hbWUiLAoJCSJ2YWx1ZUNvbCI6ImNvdW50KCopIiwKCQkieFRpdGxlIjoi6OgN8CIsCgkJInlUaXRsZSI6IrpYcM8iCgl9";
        String de = new String( new BASE64Decoder().decodeBuffer(s));
        System.out.println("解密后："+de);
    }
}
