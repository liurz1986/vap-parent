package com.vrv.vap.alarmdeal.business.asset.datasync.constant;

import java.util.HashMap;
import java.util.Map;

public class AssetDataSyncConstant {

    /**
     * 1、待编辑；2、待入库、3、入库失败 ;4、已入库(入库成功);5、已忽略
     */
    public final static int SYNCSTATUSEDIT=1; //  待编辑

    public final static int SYNCSTATUSWAIT=2; // 待入库

    public final static int SYNCSTATUSFAIL=3; // 入库失败

    public final static int SYNCSTATUSSUCCESS=4; // 已入库(入库成功)

    public final static int SYNCSTATUSNEG=5; //已忽略

    public final static String HAND_IMPORT="1"; // 手动入库

    public final static String AUTO_IMPORT="2";// 自动入库

    public  static Map<Integer,String> synchMap =new HashMap<>();

    static {
        synchMap.put(1,"待编辑");
        synchMap.put(2,"待入库");
        synchMap.put(3,"入库失败");
        synchMap.put(4,"入库成功");
        synchMap.put(5,"已忽略");
    }
    public static String getSynchStatus(Integer code){
        if(0==code){
            return "未知";
        }
        return synchMap.get(code);
    }

}
