package com.vrv.vap.data.constant;

/**
 * 数据源类型
 */
public final class SOURCE_TYPE {

    /**
     * 内置ES
     */
    public static final byte ELASTIC_BUILT = 1;

    /**
     * 内置MYSQL
     */
    public static final byte MYSQL_BUILT = 2;


    /**
     * ES 连接 (需要 connection)
     */
    public static final byte ELASTIC_CONN = 11;

    /**
     * Mysql 连接 (需要 connection)
     */
    public static final byte MYSQL_CONN = 12;
}
