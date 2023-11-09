package com.vrv.vap.admin.common.util;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class CtyUtil {



    /*
    * 楚天数据转list
    * */
    public static List<Map<String,Object>> stringToList(String data){
        Map<String,Object> resultObj= JSON.parseObject(data);
        Map<String,Object> page= JSON.parseObject(resultObj.get("page").toString());
        String str=page.get("list").toString();
        Gson gson = new Gson();
        List<Map<String,Object>> arrList = gson.fromJson(str, new TypeToken<List<Map<String, Object>>>() {}.getType());
        return  arrList;

    }
}
