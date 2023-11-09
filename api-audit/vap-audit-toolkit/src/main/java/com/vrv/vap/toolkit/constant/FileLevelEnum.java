package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 文件密级
 */
public enum FileLevelEnum {
    SUPER_SECRET("0", "绝密"),
    CONFIDENTIAL("1", "机密"),
    SECRET("2", "秘密"),
    INTERNAL("3", "内部"),
    OPEN("4", "公开"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, FileLevelEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    FileLevelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static FileLevelEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
