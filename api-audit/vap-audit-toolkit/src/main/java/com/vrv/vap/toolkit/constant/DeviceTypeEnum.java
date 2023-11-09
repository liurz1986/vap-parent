package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 设备一级类型
 */
public enum DeviceTypeEnum {
    USER_TERMINAL("0", "用户终端"),
    SERVER("1", "服务器"),
    SECURITY_CONFIDENTIALITY_PRODUCTS("2", "安全BM产品"),
    APPLICATION("3", "应用"),
    NETWORK_DEVICE("4", "网络设备"),
    OTHER_DEVICE("5", "其它设备（通用办公设备）"),
    OPERATION_TERMINAL("6", "运维终端"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, DeviceTypeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    DeviceTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static DeviceTypeEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
