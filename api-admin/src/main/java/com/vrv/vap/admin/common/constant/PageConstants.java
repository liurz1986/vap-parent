package com.vrv.vap.admin.common.constant;

/**
 * 对应前台页面的一些常量
 *
 * @author xw
 * @date 2015年11月11日
 */
public class PageConstants {
    /**
     * 对应返回json属性名
     * START从第几条开始
     * COUNT返回多少条
     */
    public static final String START = "start";
    public static final String COUNT = "count";

    /**
     * 对应返回json属性名
     * TOTAL总数
     * TOTAL_ACC实际总数
     * DATAS数据集
     */
    public static final String TOTAL = "total";
    public static final String TOTAL_ACC = "total_acc";
    public static final String DATAS = "datas";

    /**
     * SEARCH搜索
     */
    public static final String SEARCH = "s";

    /**
     * 是否
     * 1：是，0：否
     */
    public static final int IS_OK = 1;
    public static final int IS_NOT = 0;

    /**
     * 启用，禁用
     * 0：启用，1：禁用
     */
    public static final int IS_USED = 0;
    public static final int IS_NOT_USED = 1;

    /**
     * 不分页总数
     */
    public static final int NO_PAGE_COUNT = 999999;
}
