package com.vrv.vap.xc.tools;

import org.apache.commons.lang3.StringUtils;

public class TypeTools {

    public static String parseType(String type) {
        String result = "varchar";
        if(StringUtils.isNotEmpty(type)){
            switch (type) {
                case "keyword":
                    result = "varchar";
                    break;
                case "text":
                    result = "varchar";
                    break;
                case "date":
                    result = "varchar";
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
        }
        return result;
    }

    public static void main(String[] args) {
        String s = "net-floe-http-*";
        s = s.replaceAll("-\\*","");
        System.out.println(s);
    }
}
