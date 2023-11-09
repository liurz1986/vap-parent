package com.vrv.vap.xc.tools;

/**
 * IP相关工具
 * Created by lizj on 2019/10/25.
 */
public class IpTools {

    /**
     * 将数字转成ip地址
     *
     * @param ipLong
     * @return 转换后的ip地址
     */
    public static String getNumConvertIp(long ipLong) {
        long[] mask = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0) {
                ipInfo.insert(0, ".");
            }
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

    /**
     * 将ip 地址转换成数字
     *
     * @param ipStr 传入的ip地址
     * @return 转换成数字类型的ip地址
     */
    public static long getIpConvertNum(String ipStr) {
        String[] ip = ipStr.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }
}
