package com.vrv.vap.data.constant;


import com.vrv.vap.data.vo.WhereItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class SYSTEM {


    /**
     * 透传ES失败
     */
    public static final String ERROR = "{\"code\":\"9999\",\"message\":\"查询失败\"}";
    /**
     * 权限：无限制
     */
    public static final String PERMISSION_NONE = "none";
    /**
     * 权限：数据源级别
     */
    public static final String PERMISSION_SOURCE = "source";
    /**
     * 权限：字段级别
     */
    public static final String PERMISSION_FIELD = "field";

    /**
     * 系统级API
     */
    public static final String PREFIX_API = "";

    /**
     * 时间参数格式
     */
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时区
     */
    public static final String TIME_ZONE = "Asia/Shanghai";

    /**
     * ES7 版本
     */
    public static final String VERSION_SEVEN = "7";

    public static final String TRACK_TOTAL_HITS = "track_total_hits";

    public static final String ES_VERSION = "_ES_VERSION";

    /**
     * 空集合
     */
    public static final LinkedHashSet EMPTY_SET = new LinkedHashSet<>();


    /**
     * 空数组
     */
    public static final List EMPTY_LIST = new ArrayList();

    /**
     * 空数组
     */
    public static final WhereItem EMPTY_WHERE = new WhereItem("=", "1", "1");

    /**
     * 用于 CHMOD 的位数，不能重复，最高可扩展至30
     */
    public static final int MOD_ADD = 0;            // 添加
    public static final int MOD_UPDATE = 1;         // 修改
    public static final int MOD_DELETE = 2;         // 删除
    public static final int MOD_BATCH_DELETE = 3;   // 批量删除
    public static final int MOD_IMPORT = 4;         // 导入
    public static final int MOD_EXPORT = 5;         // 导出
    public static final int MOD_CUSTOM_COLUMN = 6;  // 自定义列


}
