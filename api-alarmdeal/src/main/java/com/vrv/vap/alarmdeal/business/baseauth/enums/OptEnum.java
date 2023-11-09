package com.vrv.vap.alarmdeal.business.baseauth.enums;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;


/**
 * 动作类型
 * 2023-08
 *   打印	1
 *   刻录	2
 *   访问	3
 *   运维	4
 *   网络互联	5
 */
public enum OptEnum {

    PRINT(1,"打印"),
    BURN(2,"刻录"),
    ACCESS(3,"访问"),
    MAINT(4,"运维"),
    INTER(5,"网络互联");


    public static Integer getCodeByName(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        for (OptEnum enums : OptEnum.values()) {
            if (enums.getName().equals(name)) {
                return enums.code;
            }
        }
        return null;
    }
    public static List<OptEnum> list(){
        return Arrays.asList(OptEnum.values());
    }
    public static String getNameByCode(Integer code){
        if (null == code){
            return null;
        }
        for (OptEnum enums : OptEnum.values()) {
            if (enums.getCode() == code) {
                return enums.getName();
            }
        }
        return null;
    }


    private int code;
    private String name;

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

    OptEnum(int code , String name){
        this.code=code;
        this.name=name;
    }
}
