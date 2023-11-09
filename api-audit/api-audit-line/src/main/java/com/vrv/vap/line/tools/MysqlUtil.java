package com.vrv.vap.line.tools;

import com.vrv.vap.line.model.SysMeter;
import com.vrv.vap.line.model.SysMeterAttached;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Li
 * @data 2022/1/6 14:50
 * @description 创建数据库表工具类
 */

public class MysqlUtil {
    /**
     * 创建生成表的SQL
     * @param sysMeter
     * @return
     */
    public static String createSQL(SysMeter sysMeter){
        // 创建主键集合
        List<String> priKeyList = new ArrayList<String>();
        // 创建 StringBuffer 拼接sql
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE IF NOT EXISTS `"+ sysMeter.getMeterName() +"` (\n");
        List<SysMeterAttached> meterInfo = sysMeter.getMeterInfo();
        for (int i = 0; i < meterInfo.size(); i++) {
            // 当前条数据
            SysMeterAttached sma = meterInfo.get(i);
            // 判断数据类型
            String fieldType = sma.getFieldType();
            sb.append(""+ sma.getFieldName() +"");
            if ("double".equals(fieldType)){
                // 特殊处理 `age` double(23,0) DEFAULT NULL COMMENT '年龄',
                // 追加列
                sb.append(" "+fieldType+"("+sma.getFieldLength()+","+ sma.getDecimalPoint() +") ");
            }else if ("decimal".equals(fieldType)){
                // 追加列
                sb.append(" "+fieldType+"("+sma.getFieldLength()+","+ sma.getDecimalPoint() +") ");
            }else {
                // 追加列
                sb.append(" "+fieldType+"("+sma.getFieldLength()+") ");
            }

            // 判断是否为主键 - 等于1是主键
            if (sma.isPrimaryKey()){
                // 字段名称放进去
                priKeyList.add(sma.getFieldName());
                // 判断是否允许为空 等于1是允许为空; 只有不为空的时候，需要设置
                if (sma.isNotNull()){
                    sb.append("NOT NULL ");
                }
                if(sma.isAutoIncrement()){
                    sb.append(" AUTO_INCREMENT ");
                }
                sb.append(" COMMENT '").append(sma.getFieldRemark()).append("'").append(",\n");
                // 如果到了最后一条，并且只有一个主键时
                if (i >= meterInfo.size()-1 && priKeyList.size() == 1){
                    sb.append("PRIMARY KEY ("+ priKeyList.get(0) +")");
                    sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

                }else if (i >= meterInfo.size() -1 && priKeyList.size() > 1){
                    // 最后一条，并且存在多个主键时
                    sb.append("PRIMARY KEY (");
                    // 遍历主键集合
                    for (int j = 0; j < priKeyList.size(); j++) {
                        // 最后一个时
                        if (j == priKeyList.size() -1){
                            sb.append(""+ priKeyList.get(j) +") USING BTREE \n");
                        }else {
                            sb.append(""+ priKeyList.get(j) +",");
                        }
                    }
                    sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                }
                // 非主键，直接判断是否允许为空
            }else {
                // 存在主键，并且为最后一个了
                if (priKeyList.size() > 0 && i >= meterInfo.size() -1 ){
                    // 判断是否为空 if是可以为空
                    if (!sma.isNotNull()){
                        sb.append("DEFAULT NULL COMMENT '"+ sma.getFieldRemark() +"',\n");
                    }else {
                        sb.append("NOT NULL COMMENT '"+ sma.getFieldRemark() +"',\n");
                    }
                    // 表示只有一个主键
                    if (priKeyList.size() == 1){
                        sb.append("PRIMARY KEY ("+ priKeyList.get(0) +")\n");
                        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                    }else {
                        // 最后一条，并且存在多个主键时
                        sb.append("PRIMARY KEY (");
                        // 遍历主键集合
                        for (int j = 0; j < priKeyList.size(); j++) {
                            // 最后一个时
                            if (j == priKeyList.size() -1){
                                sb.append(""+ priKeyList.get(j) +") USING BTREE \n");
                            }else {
                                sb.append(""+ priKeyList.get(j) +",");
                            }
                        }
                        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                    }
                }else {
                    // 没有就追加 判断是否为空
                    if (!sma.isNotNull()){
                        sb.append("DEFAULT NULL COMMENT '"+ sma.getFieldRemark() +"',\n");
                    }else {
                        sb.append("NOT NULL COMMENT '"+ sma.getFieldRemark() +"',\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 数据库是否存在表名
     * @param tableName
     * @return true 存在 & 错误
     * @return false 不存在
     */
    public static boolean existsTable(String tableName, Connection conn) throws SQLException {
        // 获取数据库连接和SQL执行环境
        try {
            ResultSet tables = conn.getMetaData().getTables(null, null,tableName, null);
            if (tables.next()){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }



    /**
     * 判断类型
     * varchar
     * int
     * double
     * datetime
     * decimal
     * text
     *
     * @param type
     * @return
     *//*
    private static String judgeDataType(String type){
        String result = "";
        switch (type) {
            case "keyword":
                result = "varchar";
                break;
            case "text":
                result = "varchar";
                break;
            case "date":
                result = "datetime";
                break;
            case "long":
                result = "int";
                break;
            case "double":
                result = "double";
                break;
            case "float":
                result = "float";
                break;
        }
        return result;
    };*/
    public static void main(String[] args) {
        String s = "25633.0";
        System.out.println(Integer.parseInt(s));
    }
}