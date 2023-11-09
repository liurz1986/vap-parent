package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 事件处置状态
 */
public enum EventTypeStatusEnum {
    UNDISPOSED("0", "未处置"),
    UNDER_DISPOSAL("1", "处置中"),
    DISPOSED("3", "已处置"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, EventTypeStatusEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    EventTypeStatusEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EventTypeStatusEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(EventTypeStatusEnum::getKey, EventTypeStatusEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
