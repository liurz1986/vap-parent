package com.vrv.vap.toolkit.tools;

/**
 * 区域转换
 *
 * @author xw
 * @date 2018年5月3日
 */
public class AreaTools {

    /**
     * ip正则表达式
     */
    public static final String IP_PATTERN = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";

    private static final String FF = "FF";

    private static final char[] MASK = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F'};

    private static final String toHex(int i) {
        if (i >= 255) {
            return FF;
        }
        return new String(new char[]{MASK[i >> 4], MASK[i % 16]});
    }

    /**
     * ip转数字
     *
     * @String ip
     * @return
     */
    public static long ipToNum(String ip) {
        String[] ipArr = ip.split("\\.");
        StringBuffer sb = new StringBuffer(8);
        try {
            for (String i : ipArr) {
                sb.append(toHex(Integer.valueOf(i.trim())));
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return Long.parseLong(sb.toString(), 16);
    }

    /**
     * ip number转ip string
     *
     * @String ipNum
     * @return
     */
    public static String numToIp(long ipNum) {
        StringBuffer tmp = new StringBuffer(Long.toHexString(ipNum));
        while (tmp.length() < 8) {
            tmp.insert(0, "0");
        }
        StringBuffer tmp2 = new StringBuffer(15);

        char[] chars = tmp.toString().toCharArray();
        for (int i = 0; i < chars.length; i += 2) {
            tmp.setLength(0);
            tmp.insert(0, chars[i]);
            tmp.insert(1, chars[i + 1]);
            tmp2.append(Integer.parseInt(tmp.toString(), 16)).append(".");
        }
        return tmp2.deleteCharAt(tmp2.length() - 1).toString();
    }

    /**
     * 是否为ip
     *
     * @String ip
     * @return
     */
    public static boolean isIp(String ip) {
        return ip.matches(IP_PATTERN);
    }

    /**
     * 判断ip是否在指定范围内
     *
     * @String ip
     * @String startIp
     * @String endIp
     * @return
     */
    public static boolean isInRange(String ip, String startIp, String endIp) {
        long ipNum = ipToNum(ip);
        long start = ipToNum(startIp);
        long end = ipToNum(endIp);
        return isInRange(ipNum, start, end);
    }

    /**
     * 判断ip是否在指定范围内
     *
     * @String ip
     * @String startIp
     * @String endIp
     * @return
     */
    public static boolean isInRange(long ip, String startIp, String endIp) {
        long start = ipToNum(startIp);
        long end = ipToNum(endIp);
        return isInRange(ip, start, end);
    }

    /**
     * 判断ip是否在指定范围内
     *
     * @String ip
     * @String startIp
     * @String endIp
     * @return
     */
    public static boolean isInRange(long ip, long startIp, long endIp) {
        long start = Math.min(startIp, endIp);
        long end = Math.max(startIp, endIp);
        return ip >= start && ip <= end;
    }
}
