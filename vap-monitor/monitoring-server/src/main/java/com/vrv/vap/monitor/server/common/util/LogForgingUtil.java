package com.vrv.vap.monitor.server.common.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class LogForgingUtil {
    public static String validLog(String logContent) {
        List<String> sensitiveStr = new ArrayList<>();
        sensitiveStr.add("%0a");
        sensitiveStr.add("%0A");
        sensitiveStr.add("%0d");
        sensitiveStr.add("%0D");
        sensitiveStr.add("\r");
        sensitiveStr.add("\n");
        String normalize = Normalizer.normalize(logContent, Normalizer.Form.NFKC);
        for (String str : sensitiveStr) {
            normalize.replace(str, "");
        }
        return normalize;
    }
}
