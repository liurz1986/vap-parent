package com.vrv.vap.data.util;

/**
 * 通用 SQL 构造器,支持常用数据库 MYSQL / SQLITE / SQLSERVER
 */
public class SqlBuilder {


    private String tableName;




    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT");
        return sb.toString();
//        return super.toString();
    }
}
