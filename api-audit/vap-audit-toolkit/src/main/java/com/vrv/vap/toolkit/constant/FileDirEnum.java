package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 文件传输方向
 */
public enum FileDirEnum {
    UPLOAD("1", "上传"),
    DOWNLOAD("2", "下载"),
    UNKNOWN("0", "未知");

    private final String key;
    private final String value;

    static final Map<String, FileDirEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    FileDirEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static FileDirEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
