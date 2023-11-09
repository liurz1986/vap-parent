package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class ModelUtil {
    private static String REG = "delete,drop,truncate,update,alter,delete_by_query";

    private static final Logger log = LoggerFactory.getLogger(ModelUtil.class);
    public static Map<String ,Object> buildBindParam(String params){
        Map<String,Object> param = new HashMap<String,Object>();
        if(StringUtils.isNotEmpty(params)){
            JSONArray jsonArray=JSONArray.parseArray(params);
            jsonArray.forEach(e -> {
                JSONObject object= (JSONObject)e;
                if(object.get("field") != null && StringUtils.isNotEmpty(object.get("field").toString())) {
                    param.put(object.get("field").toString(), object.get("value"));
                }
            });
        }
        return param;
    }

    public static String[][] buildTableFiledAndName(String content){
        JSONArray jsonArray=JSONArray.parseArray(content);
        String[] filed = new String[jsonArray.size()];
        String[] name = new String[jsonArray.size()];
        Map<String,Object> param = new HashMap<String,Object>();
        for(int i = 0; i < jsonArray.size();i++ ){
            JSONObject object = (JSONObject) jsonArray.get(i);
            name[i] = object.get("name") == null ? "" : object.get("name").toString();
            filed[i]  = object.get("filed") == null ? "" : object.get("filed").toString();
        }
        return new String[][]{filed,name};
    }

    public static Map<String,Object> extendBindParam(List<Map<String, Object>> list,Map<String,Object> m){
        Map result = new HashMap<>();
        if(m != null){
            result.putAll(m);
        }
        for(Map<String, Object> e : list){
            result.putAll(e);
        }
        if(result.isEmpty()){
            result.put("-1","");//添加默认值
        }
        return result;
    }

    public static Date formatData(String time,String pattern){
        if(pattern == null){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try{
            return sdf.parse(time);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return null;
    }

    public static String date2string(Date e,String pattern){
        if(pattern == null){
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(e);
    }

    public static boolean checkSql(String sql){
        if(StringUtils.isEmpty(sql)){
            return true;
        }
        String[] split = REG.split(",");
        boolean flag = true;
        for(String s : split){
            if(sql.toLowerCase().indexOf(s) > -1){
                flag = false;
                return flag;
            }
        }
        return flag;
    }

    public static List<Map<String, Object>> safeList4html(List<Map<String, Object>> list){
        List<Map<String, Object>> result = new ArrayList<>();
        list.forEach(e ->{
            Map<String, Object> data = new HashMap<>();
            e.entrySet().forEach(m ->{
                data.put(m.getKey(),m.getValue() != null ? HtmlUtils.htmlEscape(m.getValue().toString()): "");
            });
            result.add(data);
        });
        return result;
    }

}
