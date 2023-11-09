package com.vrv.vap.xc.tools;


/**
 * 设备审计信息格式化器
 * This class is used for ...
 *
 * @author taoyongbo
 * @version 1.0, 2016年1月26日 上午11:45:06
 */
public class AuditFormat {
    private static String format = " %s 由  %s 变更到  %s ;";

    private static String EMPTY = "空";

    public static void main(String[] args) {
        System.out.println(String.format(format, "资产号", "123", "000"));
    }

    /**
     * 格式化信息
     *
     * @String changeType  变更类别
     * @String beforeValue 变更前
     * @String afterValue  变更后
     * @return
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
