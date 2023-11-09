package com.vrv.vap.admin.common.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RC4 {

    private static final String ALG = new String(new byte[]{82,67,52});
    //随机密钥,随机密钥不好回溯，用固定密钥
    //private static final SecretKeySpec KEY = new SecretKeySpec(("VAP_PLATFORM").getBytes(), "RC4");
    private static final SecretKeySpec KEY = new SecretKeySpec(("VAP_PLATFORM").getBytes(), ALG);
    //private static final SecretKeySpec KEY = new SecretKeySpec(("VAP_PLATFORM_KEY").getBytes(), "AES");

    //加密
    public static String encrypt(String message) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            byte[] data = cipher.update(message.getBytes());
            return DatatypeConverter.printBase64Binary(data);
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        } catch (InvalidKeyException e) {
        } catch (NullPointerException e) {
        }
        return "";
    }

    //解密
    public static String decryption(String message) {
        try {
            byte[] data = DatatypeConverter.parseBase64Binary(message);
            //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding","BC");
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, KEY);
            byte[] textdata = cipher.update(data);
            return new String(textdata);
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        } catch (InvalidKeyException e) {
        } catch (NullPointerException e) {
        }
        return "";
    }


}
