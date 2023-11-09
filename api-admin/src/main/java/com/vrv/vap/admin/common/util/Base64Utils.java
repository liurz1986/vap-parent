package com.vrv.vap.admin.common.util;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Base64编码工具类
 *
 * @author King
 * @since 2018/12/27 16:18
 */
public class Base64Utils {

    static Charset charset = Charset.forName("UTF-8");

    public static byte[] encode(byte[] data) {
        Assert.notNull(data, "data null");
        return Base64.getEncoder().encode(data);
    }

    public static String encode2string(byte[] data) {
        return new String(encode(data));
    }

    public static byte[] encode(String data) {
        Assert.hasText(data, "data invalid");
        return Base64.getEncoder().encode(data.getBytes(charset));
    }

    public static String encode2string(String data) {
        return new String(encode(data));
    }

    public static byte[] decode(byte[] data) {
        Assert.notNull(data, "data null");
        return Base64.getDecoder().decode(data);
    }

    public static String decode2string(byte[] data) {
        return new String(decode(data), charset);
    }

    public static byte[] decode(String data) {
        Assert.notNull(data, "data null");
        return decode(data.getBytes(charset));
    }

    public static String decode2String(String data) {
        Assert.notNull(data, "data null");
        return new String(decode(data), charset);
    }

}
