package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 告警处理状态
 */
public enum AlarmDealStateEnum {
	UNTREATED("未处置", 0), 
	PROCESSING("处置中", 1), 
	PROCESSED("已处置", 3),

	UNKNOWN("未知",-1);

	private final String key;
	private final Integer value;

	static final Map<String, AlarmDealStateEnum> maps = new HashMap<>();

	static {
		Stream.of(values()).forEach(s -> maps.put(s.key, s));
	}

	AlarmDealStateEnum(String key, Integer value) {
		this.key = key;
		this.value = value;
	}

	public static AlarmDealStateEnum forString(String key){
		return maps.getOrDefault(key, UNKNOWN);
	}

	public static Map<String, Integer> getKeyToValueMap() {
		return Stream.of(values())
				.filter(enumValue -> !enumValue.equals(UNKNOWN))
				.collect(Collectors.toMap(AlarmDealStateEnum::getKey, AlarmDealStateEnum::getValue));
	}

	public String getKey() {
		return this.key;
	}

	public Integer getValue() {
		return this.value;
	}
}
