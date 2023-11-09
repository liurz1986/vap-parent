package com.vrv.vap.netflow.common.enums;


/**
 * 监视器需要返回的字段类型， type定义枚举类型
 * @author wh1107066
 * @date 2023/8/17 11:01
 */
public enum MonitorTypeEnum {
    TYPE_SUCCESS(0,"成功"),
    TYPE_FAILED(1,"失败"),
    TYPE_UNREGISTERED(2,"未注册"),

    TYPE_REG_FAILED(3,"注册审核不通过");

    private Integer type;
    private String desc;

    MonitorTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
