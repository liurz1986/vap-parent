package com.vrv.vap.netflow.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 决定日志是否持续打印
 *
 * @author wh1107066
 */
public class LoggerPrintStrategy {
    private static Map<LoggerUtil, Map<String, Integer>> indexes = new HashMap<LoggerUtil, Map<String, Integer>>();
    private LoggerUtil logger;
    private String key;

    /**
     * @param logger logger实例
     * @param key    标记某处记录日志
     */
    public LoggerPrintStrategy(LoggerUtil logger, String key) {
        if (!indexes.containsKey(logger)) {
            Map<String, Integer> keyIndex = new HashMap<String, Integer>();
            keyIndex.put(key, 0);
            indexes.put(logger, keyIndex);
        }
        Map<String, Integer> mapKeyIndex = indexes.get(logger);
        if (!mapKeyIndex.containsKey(key)) {
            mapKeyIndex.put(key, 0);
        }
        this.logger = logger;
        this.key = key;
    }

    public boolean whetherPrint() {
        Integer integer = indexes.get(logger).get(key);
        boolean result = strategy(integer);
        integer++;
        indexes.get(logger).put(key, integer);

        return result;
    }

    public int getIndex() {
        Integer integer = indexes.get(logger).get(key);
        return integer;
    }

    public String getKey() {
        return key;
    }

    public boolean strategy(int i) {
        if (i % 500 == 0) {
            return true;
        }

        return false;
    }
}

