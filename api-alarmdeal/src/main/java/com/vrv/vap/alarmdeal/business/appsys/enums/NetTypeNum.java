package com.vrv.vap.alarmdeal.business.appsys.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络类型
 * @author lps 2021/8/12
 */

public enum NetTypeNum {


    LAN("lan","局域网"),
    WAN("wan","广域网");

    private String name;

    private String code;

    NetTypeNum(String code, String name){
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
        NetTypeNum[] netTypes= NetTypeNum.values();
        for(NetTypeNum netType : netTypes){
            if(netType.code.equals(code)){
                name=netType.name;
                break;
            }
        }
        return  name;
    }

    public static String getCodeByName(String name){
        String code="";
        NetTypeNum[] netTypes= NetTypeNum.values();
        for(NetTypeNum netType : netTypes){
            if(netType.name.equals(name)){
                code=netType.code;
                break;
            }
        }
        return  code;
    }

    public static String[] getEnumNames(){
        List<String> enumName=new ArrayList<>();
        NetTypeNum[] netTypeNums= NetTypeNum.values();
        for(NetTypeNum netTypeNum : netTypeNums){
            enumName.add(netTypeNum.name);
        }
        return enumName.toArray(new String[enumName.size()]);
    }
}
