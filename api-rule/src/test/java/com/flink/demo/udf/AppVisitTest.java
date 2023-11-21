package com.flink.demo.udf;

import com.google.gson.Gson;
import com.vrv.rule.ruleInfo.udf.AppVisitFunction;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * 首访序列单元测试
 */
public class AppVisitTest {


    List<Map<String,Object>> list = new ArrayList<>();

    List<Map<String,Object>> appList = new ArrayList<>();

    @Before
    public void init(){
        for (int i = 0; i < 10; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("time_stamp",System.currentTimeMillis()+new Random().nextInt(10)*1000);
            map.put("http_res_code",getRandomResCode());
            map.put("url",getRandomUrl());
            list.add(map);
        }
        for (int i = 0; i < 1; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("app_url",getRandomUrl());
            map.put("operation_url",getRandomUrl());
            appList.add(map);
        }


    }
    private int getRandomResCode(){
        Random random = new Random();
        int [] codes = {302,200,500,404};
        int index = random.nextInt(4);
        return codes[index];
    }


    private String getRandomUrl(){
        Random random = new Random();
        String [] codes = {"/wudi/test1?page=1&count=1", "/wudi/test2?page=2&count=2", "/wudi/test3?page=3&count=3", "/wudi/test4?page=4&count=4"};
        int index = random.nextInt(4);
        return codes[index];
    }


    /**
     * 测试间隔时间筛选
     */
    @Test
    public void testInterValTime(){
        AppVisitFunction appVisitFunction = new AppVisitFunction();
        int index1 = appVisitFunction.getIndex(list,3*1000,"time_stamp");
        System.out.println(index1);
    }

    /**
     * 过滤响应编码
     */
    @Test
    public void testFilterResCode(){
        AppVisitFunction appVisitFunction = new AppVisitFunction();
        List<Map<String, Object>> resultByResCode = appVisitFunction.getResultByResCode("200,302", list);
        System.out.println(new Gson().toJson(resultByResCode));
    }



    @Test
    public void testFilterAppUrl(){
        AppVisitFunction appVisitFunction = new AppVisitFunction();
        boolean result = appVisitFunction.isContain(list, appList);
        System.out.println(result);
    }
}
