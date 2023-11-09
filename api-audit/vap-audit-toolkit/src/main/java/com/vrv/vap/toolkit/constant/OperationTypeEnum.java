package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 运维类型
 */
public enum OperationTypeEnum {
    LOCAL("0", "本地"),
    FORTRESS_MACHINE("1", "堡垒机"),
    REGIONAL_OPERATION("2", "域运维"),
    WIN_SCP("3", "winscp"),
    RING_YE("4", "融一"),
    RING_ER("5", "融二"),
    WEB("6", "web"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, OperationTypeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    OperationTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static OperationTypeEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
