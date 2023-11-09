package com.vrv.vap.common.enums;

/**
 * HTTP请求方式的枚举类
 * Created by ${huipei.x} on 2018-3-26.
 */
public enum HttpMethodEnum {
    GET(1,"GET"),
    POST(2,"POST"),
    PUT(3,"PUT"),
    DELETE(4,"DELETE");

    private int code;
    private String name;

    HttpMethodEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
