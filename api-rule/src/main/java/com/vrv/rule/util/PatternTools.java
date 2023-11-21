package com.vrv.rule.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTools {
    public static final String patternCode = "(\\w+):\\/\\/([^/:]+)(:\\d*)?([^# ]*)";
    public static String patternCheak(String url){
        Pattern pattern = Pattern.compile(patternCode);
        Matcher matcher = pattern.matcher(url);
        matcher.find();
        if (matcher.groupCount()>=4&& StringUtils.isNotBlank(matcher.group(4))){
            return matcher.group(4);
        }
        return url;
    }


}
