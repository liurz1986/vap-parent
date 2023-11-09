package com.vrv.vap.monitor.server.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanUtil {
    public static String cleanString(String aString) {
        if (aString == null) return null;
        aString = encodeChineseChar(aString);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < aString.length(); ++i) {
            stringBuilder.append(cleanChar(aString.charAt(i)));
        }
        return decodeChineseChar(stringBuilder.toString());
    }

    private static char cleanChar(char aChar) {

        // 0 - 9
        for (int i = 48; i < 58; ++i) {
            if (aChar == i) return (char) i;
        }

        // 'A' - 'Z'
        for (int i = 65; i < 91; ++i) {
            if (aChar == i) return (char) i;
        }

        // 'a' - 'z'
        for (int i = 97; i < 123; ++i) {
            if (aChar == i) return (char) i;
        }

        // other valid characters
        switch (aChar) {
            case '/':
                return '/';
            case '.':
                return '.';
            case '-':
                return '-';
            case '_':
                return '_';
            case ' ':
                return ' ';
            case '\\':
                return '\\';
            case '>':
                return '>';
            case '<':
                return '<';
            case '&':
                return '&';
            case '|':
                return '|';
            case '(':
                return '(';
            case ')':
                return ')';
            case ';':
                return ';';
            case ':':
                return ':';
            case '?':
                return '?';
            case '\'':
                return '\'';
            case '\"':
                return '\"';
            case '=':
                return '=';
            case ',':
                return ',';
            case '*':
                return '*';
            case '@':
                return '@';
            case '#':
                return '#';
        }
        /*int charValue = aChar;
        if (charValue >= 19968 && charValue <= 40869) {
            return (char) charValue;
        }*/
        return '%';
    }

    public static String[] cleanStrArray(String[] strArray) {
        if (strArray == null || strArray.length == 0) {
            return strArray;
        }

        String[] result = new String[strArray.length];
        for (int i = 0;i < strArray.length; i++) {
            result[i] = cleanString(strArray[i]);
        }
        return result;
    }

    private static String encodeChineseChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        String res = str;
        while (m.find()) {
            String strChinese = m.group();
            String strCh = "(" + (int)strChinese.charAt(0) + ")";
            res = res.replace(strChinese, strCh);
        }
        return res;
    }

    private static String decodeChineseChar(String str) {
        Pattern p = Pattern.compile("\\([1-4][0-9]{4}\\)");

        Matcher m = p.matcher(str);
        String res = str;
        while (m.find()) {
            String encodeChinese = m.group();
            int charValue = Integer.parseInt(encodeChinese.replace("(","").replace(")",""));
            res = res.replace(encodeChinese, String.valueOf((char)charValue));
        }
        return res;
    }

    private static char filter4Char(char aChar) {
        switch (aChar) {
            case '￥':
                return '%';
            case '？':
                return '%';
            case '【':
                return '%';
            case '】':
                return '%';
            case '’':
                return '%';
            case '“':
                return '%';
            case '《':
                return '%';
            case '》':
                return '%';
            case '！':
                return '%';
        }
        return aChar;
    }
}
