package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class EsHealthTools {
    private static String CLUSTER_URL = "_cluster/health";
    private static String STATUS_OFF = "red";
    public static boolean isHealth(String index){
        String res = EsCurdTools.simpleGetQueryHttp2(CLUSTER_URL);
        if(StringUtils.isEmpty(res)){
            return false;
        }
        JSONObject resObj = JSONObject.parseObject(res);
        String status = resObj.getString("status");
        if(STATUS_OFF.equals(status)){
            return false;
        }
        String indexRes = EsCurdTools.simpleGetQueryHttp2(CLUSTER_URL+"/"+index+"*");

        if(StringUtils.isEmpty(indexRes)){
            return false;
        }
        JSONObject indexResObj = JSONObject.parseObject(indexRes);
        String indexStatus = indexResObj.getString("status");
        if(STATUS_OFF.equals(indexStatus)){
            return false;
        }
        return true;
    }
}
