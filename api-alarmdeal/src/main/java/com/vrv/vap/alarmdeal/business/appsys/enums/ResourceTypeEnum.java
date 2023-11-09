package com.vrv.vap.alarmdeal.business.appsys.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lps 2021/8/12
 */

public enum ResourceTypeEnum {


    BUSINESS("1","业务"),
    MANAGEMENT("2","管理");

    private String name;

    private String code;

    ResourceTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }


    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public static String getNameByCode(String code){
        String name="";
        ResourceTypeEnum[] resourceTypeEnums=ResourceTypeEnum.values();
        for(ResourceTypeEnum resourceTypeEnum : resourceTypeEnums){
            if(resourceTypeEnum.code.equals(code)){
                name=resourceTypeEnum.name;
                break;
            }
        }
        return  name;
    }

    public static String getCodeByName(String name){
        String code="-1";
        ResourceTypeEnum[] resourceTypeEnums= ResourceTypeEnum.values();
        for(ResourceTypeEnum resourceTypeEnum : resourceTypeEnums){
            if(resourceTypeEnum.name.equals(name)){
                code=resourceTypeEnum.code;
                break;
            }
        }
        return  code;
    }


    public static String[] getEnumNames(){
        List<String> enumName=new ArrayList<>();
        ResourceTypeEnum[] resourceTypeEnums= ResourceTypeEnum.values();
        for(ResourceTypeEnum resourceTypeEnum : resourceTypeEnums){
            enumName.add(resourceTypeEnum.name);
        }
        return enumName.toArray(new String[enumName.size()]);
    }
}
