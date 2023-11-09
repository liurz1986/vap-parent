package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NoticeTypeEnum {
    REGULATORY_EVENTS("1", "督办"),
    EARLY_WARNING_REPORTING("2", "预警"),
    ORGANIZER_FEEDBACK("3", "协办"),
    JOINT_INVESTIGATION_REPORTING("4", "协查"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, NoticeTypeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    NoticeTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static NoticeTypeEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(NoticeTypeEnum::getKey, NoticeTypeEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }
    public String getValue() {
        return this.value;
    }
}
