package com.vrv.vap.alarmdeal.business.appsys.util;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.enums.InternetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.enums.NetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.enums.ResourceTypeEnum;

import java.util.List;

/**
 * @author lps 2021/8/16
 */

public class EnumTransferUtil {


    public static String nameTransfer(String key, Object object,List<BaseDictAll> secretLevels, List<BaseDictAll> protectLevels) {
        String value=object.toString();
        switch (key){
            case "secretLevel":
                value= getCodeByValue(value,secretLevels);
                break;
            case "internetType":
                value= InternetTypeNum.getCodeByName(value);
                break;
            case "protectLevel":
                value=getCodeByValue(value,protectLevels);
                break;
            case "netType":
                value= NetTypeNum.getCodeByName(value);
                break;
            default:
                break;

        }
        return value;
    }

    public static  String getCodeByValue(String value,List<BaseDictAll> datas) {
        for(BaseDictAll data : datas){
            if(value.equalsIgnoreCase(data.getCodeValue())){
                return data.getCode();
            }
        }
        return null;
    }

    public static String numTransfer(String key, Object object, List<BaseDictAll> sercretLevels, List<BaseDictAll> protectLevels) {
        String value=object.toString();
        switch (key){
            case "secretLevel":
                value= getValueByCode(value,sercretLevels);
                break;
            case "internetType":
                value= InternetTypeNum.getNameByCode(value);
                break;
            case "protectLevel":
                value= getValueByCode(value,protectLevels);
                break;
            case "netType":
                value= NetTypeNum.getNameByCode(value);
                break;
            case "resourceType":
                value= ResourceTypeEnum.getNameByCode(value);
                break;
            default:
                break;
        }
        return value;
    }

    public static String getValueByCode(String code,List<BaseDictAll> datas){
        for(BaseDictAll data : datas){
            if(code.equalsIgnoreCase(data.getCode())){
                return data.getCodeValue();
            }
        }
        return null;
    }
}
