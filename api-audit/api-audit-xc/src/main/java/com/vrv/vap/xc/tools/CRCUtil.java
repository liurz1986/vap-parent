package com.vrv.vap.xc.tools;


import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * <B>说 明</B>:CRCUtil工具类
 * <B>描 述</B>:CRCUtil工具类
 */
public class CRCUtil {

    /**
     * @InputStream in
     * @return
     */
    public static long streamCrc(InputStream in) throws IOException {
        byte[] b = new byte[1024 * 8];
        CRC32 c = new CRC32();
        int len = 0;
        while ((len = in.read(b, 0, 1024 * 8)) != -1) {
            c.update(b, 0, len);
        }
        return c.getValue();
    }

    /**
     * @byte array
     * @return
     */
    public static long getCrc(byte[] array) {
        CRC32 c = new CRC32();
        c.update(array);
        return c.getValue();
    }

    /**
     * @byte array
     * @return
     */
    public static String getCrcHexStr(byte[] array) {
        CRC32 c = new CRC32();
        c.update(array);
        return Long.toHexString(c.getValue());
    }
}
