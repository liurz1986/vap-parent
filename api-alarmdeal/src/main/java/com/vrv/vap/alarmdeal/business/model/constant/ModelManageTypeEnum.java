package com.vrv.vap.alarmdeal.business.model.constant;

import java.util.ArrayList;
import java.util.List;
// 待测试(1)、已测试(2)、发布(3)、启动(4)、停用(5)、下架(6)

public enum ModelManageTypeEnum {
    DRAFT("1","待测试"),
    VALIDATE("2","已测试"),
    DEPLOYE("3","发布"),
    START("4","启动"),
    STOP("5","停用"),
    OFFSHELF("6","下架");



    private String name;

    private String code;


    ModelManageTypeEnum(String code,String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static String getNameByCode(String code){
        String name="";
        ModelManageTypeEnum[] domainEnums=ModelManageTypeEnum.values();
        for(ModelManageTypeEnum domainEnum : domainEnums){
            if(domainEnum.code.equals(code)){
                name=domainEnum.name;
                break;
            }
        }
        return  name;
    }

    public static String getCodeByName(String name){
        String code="";
        ModelManageTypeEnum[] domainEnums= ModelManageTypeEnum.values();
        for(ModelManageTypeEnum domainEnum : domainEnums){
            if(domainEnum.name.equals(name)){
                code=domainEnum.code;
                break;
            }
        }
        return  code;
    }

    public  List<String> getEnumNames(){
        List<String> enumName=new ArrayList<>();
        ModelManageTypeEnum[] protectEnums= ModelManageTypeEnum.values();
        for(ModelManageTypeEnum protectEnum : protectEnums){
            enumName.add(protectEnum.name);
        }
        return enumName;
    }

    public  List<String> getEnumCodes(){
        List<String> enumCodes=new ArrayList<>();
        ModelManageTypeEnum[] protectEnums= ModelManageTypeEnum.values();
        for(ModelManageTypeEnum protectEnum : protectEnums){
            enumCodes.add(protectEnum.code);
        }
        return enumCodes;
    }
}
