package com.vrv.vap.monitor.constants;

/**
 * 查询会用到的一些常量
 *
 * @author xw
 * @date 2015年11月11日
 */
public class QueryTypeConstants {
    /**
     * 普通搜索
     */
    public static final int COMMON = 0;
    /**
     * 综合搜索
     */
    public static final int COMPOSITE = 1;
    /**
     * 综合搜索-综合审计
     */
    public static final int COMPOSITE4ZHAUDIT = 3;

    /**
     * 探索
     */
    public static final int EXPLORER = 2;

    /**
     * 查询或
     */
    public static final int OR = 0;
    /**
     * 查询且
     */
    public static final int AND = 1;
    /**
     * 查询非
     */
    public static final int NOT = 2;
    /**
     * 应用系统被哪些设备访问过
     */
    public static final int SYSTEM = 0;
    /**
     * 设备访问哪些应用系统
     */
    public static final int DEVICE = 1;

    /**
     * 下级查询条件
     */
    public static final String _Q = "_q";

    public static final String S_OR = "or";
    public static final String S_AND = "and";
    public static final String S_NOT = "not";
    public static final String S_MISS = "miss";
}
