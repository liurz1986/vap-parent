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
public enum BaseAuthEnum {

    PRINT(1,"打印权限审批信息",143,1),
    BURN(2,"刻录权限审批信息",144,2),
    ACCESS(3,"应用访问权限审批信息",14,3),
    MAINT(4,"网络互联权限审批信息",146,5),
    INTER(5,"运维权限审批登记信息",141,4);


    public static Integer getCodeByName(String name){
        if (StringUtils.isEmpty(name)){
            return null;
        }
        for (BaseAuthEnum enums : BaseAuthEnum.values()) {
            if (enums.getName().equals(name)) {
                return enums.code;
            }
        }
        return null;
    }
    public static List<BaseAuthEnum> list(){
        return Arrays.asList(BaseAuthEnum.values());
    }
    public static BaseAuthEnum getBaseAuthEnumByCode(Integer code){
        if (null == code){
            return null;
        }
        for (BaseAuthEnum enums : BaseAuthEnum.values()) {
            if (enums.getCode() == code) {
                return enums;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code){
        if (null == code){
            return null;
        }
        for (BaseAuthEnum enums : BaseAuthEnum.values()) {
            if (enums.getCode() == code) {
                return enums.getName();
            }
        }
        return null;
    }

    private int code;
    private String name;

    public Integer baseAuthType;
    public Integer opt;

    public Integer getOpt() {
        return opt;
    }

    public void setOpt(Integer opt) {
        this.opt = opt;
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

    public Integer getBaseAuthType() {
        return baseAuthType;
    }

    public void setBaseAuthType(Integer baseAuthType) {
        this.baseAuthType = baseAuthType;
    }

    BaseAuthEnum(int code, String name, Integer baseAuthType,Integer opt) {
        this.code = code;
        this.name = name;
        this.baseAuthType = baseAuthType;
        this.opt = opt;
    }
}
