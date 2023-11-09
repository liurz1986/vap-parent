package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 部门密级
 */
public enum DepartmentLevelEnum {
    SUPER_SECRET("0", "绝密"),
    CONFIDENTIAL("1", "机密"),
    SECRET("2", "秘密"),
    INTERNAL("3", "内部"),
    OPEN("4", "非密"),

    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, DepartmentLevelEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    DepartmentLevelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static DepartmentLevelEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(DepartmentLevelEnum::getKey, DepartmentLevelEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
