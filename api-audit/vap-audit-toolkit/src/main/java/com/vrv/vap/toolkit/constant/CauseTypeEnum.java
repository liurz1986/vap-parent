package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CauseTypeEnum {
    NORMAL_BUSINESS_BEHAVIOR("1", "正常业务行为"),
    FAILURE_TO_COMPLETE_APPROVAL_PROCEDURES("2", "未履行审批手续"),
    FAILURE_REGISTER_CHANGE_BASIC_INFORMATION_APPROVAL_INFORMATION("3", "未及时登记或变更基础信息和审批信息"),
    PERSONNEL_NOT_AWARE_OF_RELEVANT_REGULATIONS("4", "人员不知悉相关规定"),
    MINERALISATION_BY_PERSONNEL("5", "人员误操作"),
    INTENTIONAL_VIOLATION_BY_PERSONNEL("6", "人员故意违规"),
    IMPROPER_OPERATION_AND_MAINTENANCE_MANAGEMENT("7", "运维管理不当"),
    PROTECTION_STRATEGY_FAILURE("8", "防护策略失效"),
    IMPROPER_EQUIPMENT_MANAGEMENT ("9", "设备管理不当"),
    INFECTED_WITH_VIRUSES_OR_MALICIOUS_PROGRAMS("10", "感染病毒或恶意程序"),
    SYSTEM_OWN_VULNERABILITIES("11", "系统自身漏洞"),
    OTHER_CAUSES("12", "其他原因"),
    FALSE_ALARM("13", "误报"),
    UNKNOWN("-1", "未知");

    private final String key;
    private final String value;

    static final Map<String, CauseTypeEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    CauseTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static CauseTypeEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(CauseTypeEnum::getKey, CauseTypeEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
