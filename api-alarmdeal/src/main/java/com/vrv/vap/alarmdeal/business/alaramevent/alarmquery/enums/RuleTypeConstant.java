package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums;

/**
 * 统计告警类型
 *
 */
public class RuleTypeConstant {
    //登录异常
    //5c5a9afd07964f9aa7e2a014d0e25087  非管理员本人本地登录主机操作系统
    //9ad74a3ab38c4784b32afc1619709d02  非本人本地登录终端操作系统
    public static final String LOGIN_EXCEPTION = "5c5a9afd07964f9aa7e2a014d0e25087,9ad74a3ab38c4784b32afc1619709d02";
    //打印刻录异常
    //cb31387d9b7c4c2a9361005c4ed5dc83  用户异常打印刻录文件
    //8981204e8d4944cfb240b1033950f1f9  管理员使用运维终端大量刻录文件
    //156cafba8f06448283f6acbb49474020 管理员在用户终端、服务器上打印或刻录本地文件
    public static final String PRINT_BURN_EXCEPTION  = "cb31387d9b7c4c2a9361005c4ed5dc83,8981204e8d4944cfb240b1033950f1f9,156cafba8f06448283f6acbb49474020";
    //29c99de46b4a49968445d29ff2c0efe1  异常开关机
    public static final String ABNORMAL_SHUTDOWN_EXCEPTION  = "29c99de46b4a49968445d29ff2c0efe1";
    //637049076f49432494188c71338a1e43   终端防护软件客户端（防病毒/安全登录/主机审计/三合一）不在线
    public static final String NOT_ONLINE_EXCEPTION = "637049076f49432494188c71338a1e43";

}
