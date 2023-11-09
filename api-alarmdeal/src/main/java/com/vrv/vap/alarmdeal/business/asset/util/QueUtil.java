package com.vrv.vap.alarmdeal.business.asset.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * 队列工具类
 *
 * 1. 数据同步kafka资产数据
 * 2. 数据同步kafka数据信息数据
 * 3. 数据同步kafka应用系统数据
 * 2022-06-20
 */
public class QueUtil {

    public static final String ASSET="asset";
    public static final String DATAINFO="data";
    public static final String APP="app";


    private static final SynchronousQueue<Map<String,Object>> assetRefQue = new SynchronousQueue<Map<String,Object>>();


    public static void assetRefQuePut(String key,Object data){
        try {
            Map<String,Object> queData = new HashMap<>();
            queData.put("key",key);
            queData.put("data",data);
            assetRefQue.put(queData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static Map<String,Object> assetRefQuePoll(){
        return assetRefQue.poll();
    }

}
