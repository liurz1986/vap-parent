package com.vrv.vap.toolkit.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AssetTypeGroupEnum {
    ASSET_HOST("assetHost","asset-Host"),
    ASSET_SERVICE("assetService","asset-service"),
    ASSET_NETWORK("assetNetworkDevice","asset-NetworkDevice"),
    ASSET_SAFE("assetSafeDevice","asset-SafeDevice"),
    ASSET_MAINTEN("assetMaintenHost","asset-MaintenHost"),
    OTHER_ASSET("otherAsset","asset-OfficeDevice-WriterMachine,asset-OfficeDevice-printer,asset-USBMemory-classified"),

    UNKNOWN("-1","未知");

    private final String key;
    private final String value;

    static final Map<String, AssetTypeGroupEnum> maps = new HashMap<>();

    static {
        Stream.of(values()).forEach(s -> maps.put(s.key, s));
    }

    AssetTypeGroupEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static AssetTypeGroupEnum forString(String key){
        return maps.getOrDefault(key, UNKNOWN);
    }

    public static Map<String, String> getKeyToValueMap() {
        return Stream.of(values())
                .filter(enumValue -> !enumValue.equals(UNKNOWN))
                .collect(Collectors.toMap(AssetTypeGroupEnum::getKey, AssetTypeGroupEnum::getValue));
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }
}
