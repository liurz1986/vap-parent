package com.vrv.vap.common.enums;

/**
 * 排序规则枚举
 * Created by ${huipei.x} on 2018-3-26.
 */
public  enum OrderEnum implements BaseEnum{
    DESC(1,"DESC"),
    ASC(2,"ASC");

    private int code;
    private String name;

    OrderEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (OrderEnum orderEnum : OrderEnum.values()) {
            if (orderEnum.code==code) {
                return orderEnum.name;
            }
        }
        return null;
    }

    @Override
    public int getCode() {
        return code;
    }




        @Override
        public String getName() {
        return name;
    }


}
