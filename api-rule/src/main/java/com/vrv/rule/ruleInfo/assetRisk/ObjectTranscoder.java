package com.vrv.rule.ruleInfo.assetRisk;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.lang3.StringUtils;

public class ObjectTranscoder {

	public static byte[] serialize(Object value) {  
        if (value == null) {  
            throw new NullPointerException("Can't serialize null");  
        }  
        byte[] rv=null;  
        ByteArrayOutputStream bos = null;  
        ObjectOutputStream os = null;  
        try {  
            bos = new ByteArrayOutputStream();  
            os = new ObjectOutputStream(bos);  
            os.writeObject(value);  
            os.close();  
            bos.close();  
            rv = bos.toByteArray();  
        } catch (IOException e) {  
            throw new IllegalArgumentException("Non-serializable object", e);  
        } finally {  
        	try {
	        	 if(os!=null)os.close();
	        	 if(bos!=null)bos.close();
       	 	}catch (Exception e2) {
       		 e2.printStackTrace();
			}  
        }  
        return rv;  
    }  

    public static Object deserialize(byte[] in) {  
       
    	
    	Object rv= new Object();  
        ByteArrayInputStream bis = null;  
        ObjectInputStream is = null;  
        try {  
            if(in != null) {  
            	is = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(in)));
                rv=is.readObject();  
                is.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();
         }finally {  
        	 try {
	        	 if(is!=null)is.close();
	        	 if(bis!=null)bis.close();
        	 } catch (Exception e2) {
        		 e2.printStackTrace();
 			 }
         }
        return rv;  
    }  

    
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");
 
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }
    
    public static byte[] toByteArray(String hexString) {
        if (StringUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");
 
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }



}
