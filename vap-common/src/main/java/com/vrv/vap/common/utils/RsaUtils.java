package com.vrv.vap.common.utils;

import cn.hutool.core.codec.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加密解密
 *
 * @author wh1107066
 */
public class RsaUtils {
    /**
     * Rsa 私钥信息
     */
    static final String privateKey = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAJM3oYcVcf3DAtjEGVc" +
            "+96S1y6Vc0bJmBeQDVxkJEYiFwPQis00OUc7YIugWmaFpjO7v9oTcP95M/fdQ3WVu6Mna4u" +
            "+8a1FQx6njMAzSmtZLs8ZsdGmFxCLCzWUZMwM2EnWt5eRRpnLzMI2toCTz2xflKDtJ/P3z1OC25tLn7MX3AgMBAAECgYEAj+drT55Y5RMA718Q3kXA0RKb1DLdECPGUlIpi2Ff8DG+oWZiGkqLEUQZKwEcf7mrd8y9DrY1AFoGTwCOyh4WEeWaVYEAgtUL49dDApmGATGhtuHS6jDAnaSpAY4A7nUwrR7suNkLF2kAXG3aroRIPUFCE2zoxfpTai0g22v/9UECQQDyhSEuuQVrQZRCipsSZMOW3hYXSp9lhkLGNBGQl55jfFlgsH2i0A+hCeyMNMrKNQJbfsSd/9x5SmaAUbmqOy8HAkEAm2ZrB1CJLMu7xBapyEwGYj9BqGEiyQ8WMW7i9WliapxNpbtBn7E/Av8aG8W6J2JJpCq+W8L/4gDM/q4xLKQFkQJBALCNFnELJNTGMwaWHPow/Opx1ycxngSszyO3eCoJFrdaKT7ofS3vxdD4hoozTIYUPRkamkxjnb922FQGKuGwoDUCQQCWVdGK5P2fyYTfoXEk5W9zknCJXVdSnPbCYdzMv+PG8WkowOwuekFUO8hdP77kJPDLEdLQYOg9Ers+UvQdlJwRAkEA7yU6PGPCQa91/NKixuVBFTCMMcrx0CjFnRmTg2A5DVPaDdo04ItWqyeMVaBlHOd87ne4TOioJVNaz4NfRcKB9A==";


    /**
     *  公钥信息
     */
    static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTN6GHFXH9wwLYxBlXPvektculXNGyZgXkA1cZCRGIhcD0IrNNDlHO2CLoFpmhaYzu7/aE3D/eTP33UN1lbujJ2uLvvGtRUMep4zAM0prWS7PGbHRphcQiws1lGTMDNhJ1reXkUaZy8zCNraAk89sX5Sg7Sfz989TgtubS5+zF9wIDAQAB";

    /**
     * 私钥解密
     *
     * @param text 待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String text) throws Exception {
        return decryptByPrivateKey(privateKey, text);
    }

    /**
     * 公钥解密
     *
     * @param publicKeyString 公钥
     * @param text            待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String publicKeyString, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.decode(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyString 私钥
     * @param text             待加密的信息
     * @return 加密后的文本
     */
    public static String encryptByPrivateKey(String privateKeyString, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encode(result);
    }

    /**
     * 私钥解密
     *
     * @param privateKeyString 私钥
     * @param text             待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String privateKeyString, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decode(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.decode(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param publicKeyString 公钥
     * @param text            待加密的文本
     * @return 加密后的文本
     */
    public static String encryptByPublicKey(String publicKeyString, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64.decode(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encode(result);
    }

    /**
     * 构建RSA密钥对
     *
     * @return 生成后的公私钥信息
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.encode(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.encode(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }
}
