package com.vrv.vap.admin.common.enums;

public enum SuperviseDataTypeEnum {
    ONE(1, "监管事件数据"),
    TWO(2, "事件处置数据"),
    THREE(3, "事件线索信息"),
    FOUR(4, "协查请求"),
    FIVE(5, "协办结果"),
    SIX(6, "预警响应信息"),
    SEVEN(7, "策略变更信息"),
    EIGHT(8, "对象刻画信息"),
    RUN_STATE(100, "运行状态");

    private Integer code;
    private String name;

    SuperviseDataTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SuperviseDataTypeEnum getByCode(Integer code) {
        for (SuperviseDataTypeEnum dataTypeEnum : values()) {
            if (dataTypeEnum.getCode().equals(code)) {
                return dataTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
