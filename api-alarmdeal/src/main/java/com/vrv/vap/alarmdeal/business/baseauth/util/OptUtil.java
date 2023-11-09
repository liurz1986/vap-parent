package com.vrv.vap.alarmdeal.business.baseauth.util;
import com.vrv.vap.alarmdeal.business.baseauth.enums.OptEnum;



/**
 * 动作类型
 * 2023-08
 * @author liurz
 */
public class OptUtil {

   public static boolean isExist(Integer code){
       // 不填不校验
       if(null == code){
           return true;
       }
       String name=  OptEnum.getNameByCode(code);
       if(null == name){
           return false;
       }
       return true;
   }
}
