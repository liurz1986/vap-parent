package com.vrv.vap.admin.common.util;

import org.springframework.util.Assert;

/**
 * 十六进制工具类
 *
 * @author King
 * @since 2018/12/27 16:31
 */
public class HexUtils {

    private static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] data) {
        Assert.notNull(data, "data null");
        return new String(toHex(data));
    }

    private static char[] toHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return out;
    }


    public static byte[] hex2byte(String str)
    {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();

        if ((len == 0) || (len % 2 == 1)) {
            return null;
        }

        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[(i / 2)] = (byte)Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b; } catch (Exception e) {
        }
        return null;
    }


}
