package com.vrv.vap.admin.common.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;


import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;


public class UisaDecodeUtil {
    private static final int MAX_DECRYPT_BLOCK = 128;//最大解密密文大小
    private static final String KEY_ALGORITHM = "RSA";//秘钥算法
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";//签名算法
    public static final String CHARSER = "UTF-8";

    public static String parseUIAS(String cryptoText, InputStream is, String decrypto) {
        String result = "";
        String pkey = "";
        try{
            byte[] b = new byte[is.available()];
            is.read(b);
            is.close();
            pkey = new String(b, UisaDecodeUtil.CHARSER);
            pkey = b2s(pkey);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(null != is){
                is = null;
            }
        }
        try {
            if(!"".equals(pkey)){
                cryptoText = b2s(cryptoText);
                byte[] data = UisaDecodeUtil.decryptBASE64(cryptoText);
                byte[] dedata = UisaDecodeUtil.decryptByPubKey2(data, pkey);
                if(null != dedata){
                    result = new String(dedata, UisaDecodeUtil.CHARSER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean verifySign(byte[] data, String pKey, String sign) throws Exception {
        byte[] keyBytes = decryptBASE64(pKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);
        return signature.verify(decryptBASE64(sign));
    }

    //公钥解密：bouncycastle进行
    public static byte[] decryptByPubKey2(byte[] text, String pkey) throws Exception {
        byte[] data = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream(MAX_DECRYPT_BLOCK);
        try {
            byte[] keyBytes = decryptBASE64(pkey);
            AsymmetricKeyParameter akp = PublicKeyFactory.createKey(keyBytes);
            RSAKeyParameters rsaAKp = (RSAKeyParameters)akp;
            BigInteger modulus = rsaAKp.getModulus();//模数
            BigInteger exponent = rsaAKp.getExponent();//指数

            RSAKeyParameters pubParameters = new RSAKeyParameters(false, modulus, exponent);
            AsymmetricBlockCipher eng = new RSAEngine();
            eng = new PKCS1Encoding(eng);
            eng.init(false, pubParameters);
            int j = 0;
            int l1 = text.length;
            int l2 = j * MAX_DECRYPT_BLOCK;
            int l3 = l1 - l2;
            while (l3 > 0) {
                int aaa = l3 > MAX_DECRYPT_BLOCK ? MAX_DECRYPT_BLOCK : l3;
                byte[] d1 = eng.processBlock(text, l2, aaa);
                bout.write(d1);
                j++;

                l2 = j * MAX_DECRYPT_BLOCK;
                l3 = l1 - l2;
            }
            data = bout.toByteArray();
        } catch (Exception e) {
            throw e;
        }
        return data;
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key.getBytes(CHARSER));
    }

    public static String b2s(String binStr) {
        String[] tempStr = StrToStrArray(binStr);
        char[] tempChar = new char[tempStr.length];
        for (int i = 0; i < tempStr.length; i++) {
            tempChar[i] = BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }

    private static String[] StrToStrArray(String str) {
        int len = str.length() / 8;
        String[] arr = new String[len];
        for (int i = 0; i < len; i++) {
            arr[i] = str.substring(8 * i, 8 * (i + 1));
        }
        return arr;
    }

    private static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }

    private static int[] BinstrToIntArray(String binStr) {
        char[] temp = binStr.toCharArray();
        int[] result = new int[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = temp[i] - 48;
        }
        return result;
    }

//    public static void main(String[] atgs){
//        String str = "{\"result\":\"01001011010110010100100101000001010100010110110001101101011101110111011101101100011001010111010001100010010010010101000101101100011110000110111101100001010010010100110100110111010010100101011100110100001110010100101100110100010101110110001101001111010101010011010101110000001101010110101101001001011000110110110101001100001101110111011101110011001010110110010101000010011011100111000001010101001110000101100101010111010000100110011101110010010010110101101001110000011011110011001101001100010100110101101001001111001100000100101001110001010010110111000101010110011001100111010001010101010001010100001100110111010010100101000101011001010001110011011101101100011011110100111000110000011101100011001101001110010011100110001101000101001100000110100100111000011010110011100000111000010011010101011101110011011101110100101001110101001110010100100101000001011011010110100101110101010001000100110101100011011101010111001001010111011100110100011001110001011100000011011001110011011100010111001001100101001101010011100101010010001100100110001101110110011011000110101101101101010100000011010001101001010101100011001101101011010101100010111101101110011110010110111100110011010011010011010101000001010000110100001001001010001011110111000001001001010001010100100101011010011011000101010101010000001110000101010001111001001011110110010001101000001100010111000101010010011100010101010100111101\",\"appCode\":\"uiasDemo\"}";
//        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        try {
//            Map<String, Object> map = objectMapper.readValue(str, Map.class);
//            String UIASFilePath = "F:\\vrv\\admin-feature-dev\\api-admin\\src\\main\\resources\\uisaKey";
//            String aa = parseUIAS((String) map.get("result"), (String) map.get("appCode"), UIASFilePath, "pkcs1");
//            System.out.println(aa);
//        }catch (IOException e){
//        }
//        //dat文件存放路径
//        //{"success":false,"message":"操作失败","jitTimestamp":1525833278613}
//    }

}




