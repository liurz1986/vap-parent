package com.vrv.vap.monitor.server.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlCheckUtil {
    public static boolean checkSql(String str) {
        Pattern pattern = Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count" +
                "|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }


    public static String filterCol(String str) {
       return str.replaceAll("\\b(exec|insert|select|drop|grant|alter|delete|update" +
                "|chr|mid|master|truncate)\\b|(\\*|;|\\+|'|%)","");
    }

    public static boolean checkSqlTableName(String str) {
        Pattern pattern = Pattern.compile("[^a-zA-Z_]");
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
