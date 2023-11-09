package com.vrv.vap.line.tools;

/**
 * Created by lil on 2018/4/13.
 */
public class AuditFormat {

    private static String format = " %s 由  %s 变更到  %s ;";

    private static String EMPTY = "空";

    public static void main(String[] args) {
        System.out.println(String.format(format, "资产号", "123", "000"));
    }

    /**
     * 格式化信息
     */
    public static String format(String changeType, String beforeValue, String afterValue) {
        if (null != beforeValue
                && 0 == beforeValue.length()) {
            beforeValue = EMPTY;
        }
        if (null != afterValue
                && 0 == afterValue.length()) {
            afterValue = EMPTY;
        }
        return String.format(format, changeType, beforeValue, afterValue);
    }

    public static String format(String changeType, Object beforeValue, Object afterValue) {
        return String.format(format, changeType, beforeValue, afterValue);
    }
}
