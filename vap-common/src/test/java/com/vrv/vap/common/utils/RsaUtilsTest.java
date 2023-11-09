package com.vrv.vap.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author wh1107066
 * @date 2021/7/9 15:52
 */
class RsaUtilsTest {

    @Test
    void generateKeyPair() throws Exception {
        RsaUtils.RsaKeyPair rsaKeyPair = RsaUtils.generateKeyPair();
        String publicKey = rsaKeyPair.getPublicKey();
        String privateKey = rsaKeyPair.getPrivateKey();
        System.out.println(publicKey);
        System.out.println("------------------");
        System.out.println(privateKey);
        assertNotNull(publicKey, "");
        assertNotNull(privateKey);
    }

    @Test
    void decryptByPrivateKey() throws Exception {
        String privateKeyString = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAJM3oYcVcf3DAtjEGVc" +
                "+96S1y6Vc0bJmBeQDVxkJEYiFwPQis00OUc7YIugWmaFpjO7v9oTcP95M/fdQ3WVu6Mna4u" +
                "+8a1FQx6njMAzSmtZLs8ZsdGmFxCLCzWUZMwM2EnWt5eRRpnLzMI2toCTz2xflKDtJ/P3z1OC25tLn7MX3AgMBAAECgYEAj+drT55Y5RMA718Q3kXA0RKb1DLdECPGUlIpi2Ff8DG+oWZiGkqLEUQZKwEcf7mrd8y9DrY1AFoGTwCOyh4WEeWaVYEAgtUL49dDApmGATGhtuHS6jDAnaSpAY4A7nUwrR7suNkLF2kAXG3aroRIPUFCE2zoxfpTai0g22v/9UECQQDyhSEuuQVrQZRCipsSZMOW3hYXSp9lhkLGNBGQl55jfFlgsH2i0A+hCeyMNMrKNQJbfsSd/9x5SmaAUbmqOy8HAkEAm2ZrB1CJLMu7xBapyEwGYj9BqGEiyQ8WMW7i9WliapxNpbtBn7E/Av8aG8W6J2JJpCq+W8L/4gDM/q4xLKQFkQJBALCNFnELJNTGMwaWHPow/Opx1ycxngSszyO3eCoJFrdaKT7ofS3vxdD4hoozTIYUPRkamkxjnb922FQGKuGwoDUCQQCWVdGK5P2fyYTfoXEk5W9zknCJXVdSnPbCYdzMv+PG8WkowOwuekFUO8hdP77kJPDLEdLQYOg9Ers+UvQdlJwRAkEA7yU6PGPCQa91/NKixuVBFTCMMcrx0CjFnRmTg2A5DVPaDdo04ItWqyeMVaBlHOd87ne4TOioJVNaz4NfRcKB9A==";
//      待解密文档
        String text = "hA/3uVljQ7kMXUz+CoUhWnR2MPeW2VQZk6CTs9qod3YCL1ytI2RXy9571hyJRu4Uscifk+WNBn2CMU45l4Hp0DvarDneW/b92fJ/+qAkAbOFIJyyiVA6mQ2gCW/a/99SBb9OmWv6PxSnLP4gtoTt02YzhRdBFJzuovBzHJ0F96A=";
        String actual = RsaUtils.decryptByPrivateKey(privateKeyString, text);
        String expected = "abcd";
        assertEquals(expected, actual, "解密成功");
        System.out.println("要解密字符串:");
        System.out.println(text);
        System.out.println("私钥解密后的值为:");
        System.out.println(expected);
    }

    @Test
    void encryptByPublicKey() throws Exception {
        String publicKeyString =
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTN6GHFXH9wwLYxBlXPvektculXNGyZgXkA1cZCRGIhcD0IrNNDlHO2CLoFpmhaYzu7/aE3D/eTP33UN1lbujJ2uLvvGtRUMep4zAM0prWS7PGbHRphcQiws1lGTMDNhJ1reXkUaZy8zCNraAk89sX5Sg7Sfz989TgtubS5+zF9wIDAQAB";
        String text = "abcd";
        String actual = RsaUtils.encryptByPublicKey(publicKeyString, text);
        System.out.println("abcd使用公钥加密后的值为:");
        System.out.println(actual);
        assertNotNull(actual, "加密成功");
    }
}