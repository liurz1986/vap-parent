package com.vrv.vap.alarmdeal.business.appsys.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *接入方式
 * @author lps 2021/8/12
 */
public enum InternetTypeNum {

    REMOTE("remote","远程终端(群)"),
    INTERNET("internet","网络接入");

    private String name;

    private String code;

    InternetTypeNum(String code,String name){
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
        InternetTypeNum[] internetTypeNums=InternetTypeNum.values();
        for(InternetTypeNum internetTypeNum : internetTypeNums){
            if(internetTypeNum.code.equals(code)){
                name=internetTypeNum.name;
                break;
            }
        }
        return  name;
    }

    public static String getCodeByName(String name){
        String code="";
        InternetTypeNum[] internetTypeNums= InternetTypeNum.values();
        for(InternetTypeNum internetTypeNum : internetTypeNums){
            if(internetTypeNum.name.equals(name)){
                code=internetTypeNum.code;
                break;
            }
        }
        return  code;
    }

    public static String[] getEnumNames(){
        List<String> enumName=new ArrayList<>();
        InternetTypeNum[] internetTypeNums= InternetTypeNum.values();
        for(InternetTypeNum internetTypeNum : internetTypeNums){
            enumName.add(internetTypeNum.name);
        }
        return enumName.toArray(new String[enumName.size()]);
    }
    
    
    
}
