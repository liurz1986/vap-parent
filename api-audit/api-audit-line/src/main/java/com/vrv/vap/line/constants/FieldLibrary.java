package com.vrv.vap.line.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FieldLibrary {

    private static Set<String> DATE_SET = new HashSet<String>(Arrays.asList(new String[]{"date", "time", "year", "datetime", "timestamp"}));
    // 长整型 "TINYINT", 布尔型 "BIT" 认为是 KEYWORD
    private static Set<String> LONG_SET = new HashSet<String>(Arrays.asList(new String[]{"smallint", "mediumint", "int", "integer", "bigint"}));
    // 浮点型
    private static Set<String> DOUBLE_SET = new HashSet<String>(Arrays.asList(new String[]{"float", "double", "decimal"}));
    // 长文本
    private static Set<String> TEXT_SET = new HashSet<String>(Arrays.asList(new String[]{"text", "tinytext", "mediumtext", "longtext"}));
    // 关键字
    private static Set<String> KEYWORD_SET = new HashSet<String>(Arrays.asList(new String[]{"varchar", "bit", "tinyint", "char", "linestring"}));

    public static String toElasticType(String type) {
        if (KEYWORD_SET.contains(type)) {
            return "keyword";
        }
        if (DATE_SET.contains(type)) {
            return "date";
        }
        if (LONG_SET.contains(type)) {
            return "long";
        }
        if (DOUBLE_SET.contains(type)) {
            return "double";
        }
        if (TEXT_SET.contains(type)) {
            return "text";
        }
        return "keyword";
    }

}
