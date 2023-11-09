package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 事件类型
 */
public enum EventTypeEnum {
    MANAGEMENT_ABNORMAL("1", "管理配置违规监管事件"),
    NETWORK_ABNORMAL("2", "网络和应用系统异常监管事件"),
    USER_BEHAVIOR_ABNORMAL("3", "用户行为异常监管事件"),
    ADMIN_BEHAVIOR_ABNORMAL("7", "管理员行为异常监管事件"),
    EXTERNAL_BOUNDARY("8", "外部边界异常监管事件"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, EventTypeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    EventTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EventTypeEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(EventTypeEnum::getKey, EventTypeEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
