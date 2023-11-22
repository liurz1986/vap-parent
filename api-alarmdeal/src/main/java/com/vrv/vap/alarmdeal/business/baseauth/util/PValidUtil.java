package com.vrv.vap.alarmdeal.business.baseauth.util;

import java.util.HashSet;
import java.util.regex.Pattern;

public class PValidUtil {
    public static boolean isIPValid(String ips) {
        String[] split = ips.split(",");
        if (ips.contains(",")&&split.length==1){
            return false;
        }
        String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        for (String s:split){
            boolean matches = Pattern.matches(ipPattern, s);
            if (!matches){
                return false;
            }
        }
        return true;
    }
    public static boolean hasDuplicate(String ips) {
        String[] split = ips.split(",");
        HashSet<String> set = new HashSet<>();
        for (String s : split) {
            if (!set.add(s)) {
                return true;
            }
        }
        return false;
    }

}
