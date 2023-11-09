package com.vrv.vap.netflow.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {
    private static final String UNKNOWN = "unknown";

    /**
     * 将ip转换成整型
     * 【官方】
     * @param ip
     * @return
     */
    public static Long ip2int(String ip) {
        Long num = 0L;
        if (ip == null) {
            return num;
        }
        try {
            ip = ip.replaceAll("[^0-9\\.]", ""); // 去除字符串前的空字符
            String[] ips = ip.split("\\.");
            if (ips.length == 4) {
                num = Long.parseLong(ips[0], 10) * 256L * 256L * 256L + Long.parseLong(ips[1], 10) * 256L * 256L + Long.parseLong(ips[2], 10) * 256L + Long.parseLong(ips[3], 10);
                num = num >>> 0;
            }
        } catch (NullPointerException ex) {
            System.out.println(ip);
        }

        return num;
    }


    public static String getIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xFor = request.getHeader("X-Forwarded-For");
        if(StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xFor.indexOf(",");
            if(index != -1){
                return xFor.substring(0,index);
            }else{
                return xFor;
            }
        }
        xFor = xip;
        if(StringUtils.isNotEmpty(xFor) && !UNKNOWN.equalsIgnoreCase(xFor)){
            return xFor;
        }
        if (validIp(xFor)) {
            xFor = request.getHeader("Proxy-Client-IP");
        }
        if (validIp(xFor)) {
            xFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (validIp(xFor)) {
            xFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (validIp(xFor)) {
            xFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (validIp(xFor)) {
            xFor = request.getRemoteAddr();
        }
        return xFor;
    }

    private static boolean validIp(String ip){
        return StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip);
    }
}
