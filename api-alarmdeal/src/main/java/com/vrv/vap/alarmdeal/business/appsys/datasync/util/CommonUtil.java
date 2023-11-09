package com.vrv.vap.alarmdeal.business.appsys.datasync.util;

import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommonUtil {

    public static String getAppStrateryValue(List<TbConf> datas , String key){
        for(TbConf tbConf : datas){
            if(key.equals(tbConf.getKey())){
                return tbConf.getValue();
            }
        }
        return  null;
    }

    public static boolean checkMapKey(Map<String,List<String>> map , String keyStr){
        AtomicBoolean result = new AtomicBoolean(false);
        map.forEach((key,value)->{
            if(key.contains(keyStr)){
                result.set(true);
            }
        });
        return result.get();
    }
}
