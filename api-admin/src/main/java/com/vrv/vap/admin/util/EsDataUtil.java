package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.github.iamxwaa.elasticsearch.core.entry.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EsDataUtil {
    private static final Logger log = LoggerFactory.getLogger(EsDataUtil.class);

    public static List<Map<String, Object>> gen(EsResult result, JSONArray aggs,Object prename){
        log.info("####解析es聚合结果开始");
        List<Map<String, Object>> list = new ArrayList<>();
        JSONObject aggregations = result.getAggregations();
        String pre = prename == null ? "" : prename.toString();
        if(aggregations == null){
            SearchHit[] hits = result.getHits();
            int var8 = hits.length;
            for(int var9 = 0; var9 < var8; ++var9) {
                SearchHit hit = hits[var9];
                list.add(hit.getSource());
            }
            if(list.size() > 0){
                list.get(0).put(pre+"resultTotal",result.getTotal());
            }else{
                Map<String, Object> map = new HashMap<>();
                map.put(pre+"resultTotal",result.getTotal());
                list.add(map);
            }
            return list;
        }
        if(aggs == null){
            log.error("报表聚合未配置");
            return list;
        }
        //解析第一层
        JSONObject a1 = (JSONObject)aggs.get(0);
        String key1 = a1.getString("name");
        String key2 = "";
        if(aggs.size() > 1){
            JSONObject a2 = (JSONObject)aggs.get(1);
            key2 = a2.getString("name");
        }
        if(aggregations.containsKey(key1)){
            JSONObject ob = aggregations.getJSONObject(key1);
            if(ob.containsKey("buckets")){
                //第一层为 buckets
                JSONArray buckets = ob.getJSONArray("buckets");
                for(Object e : buckets){
                    JSONObject s = (JSONObject)e;
                    if(StringUtils.isNotEmpty(key2) && s.containsKey(key2)){
                        //包含第二层
                        JSONObject ob2 = s.getJSONObject(key2);
                        if(ob2.containsKey("buckets")){
                            //第二层为 buckets
                            JSONArray buckets2 = ob2.getJSONArray("buckets");
                            buckets2.forEach(e2 ->{
                                Map<String, Object> d = new HashMap<>();
                                JSONObject s2 = (JSONObject)e2;
                                s.entrySet().forEach(sn ->{
                                    d.put(pre+sn.getKey()+"_0",sn.getValue());
                                });
                                s2.entrySet().forEach(en ->{
                                    d.put(pre+en.getKey()+"_1",en.getValue());
                                });
                                list.add(d);
                            });
                        }else{
                            //第二层无 buckets
                            Map<String, Object> d = new HashMap<>();
                            s.entrySet().forEach(sn ->{
                                d.put(pre+sn.getKey()+"_0",sn.getValue());
                            });
                            ob2.entrySet().forEach(en ->{
                                d.put(pre+en.getKey()+"_1",en.getValue());
                            });
                            list.add(d);
                        }
                    }else{
                        //不包含第二层
                        Map<String, Object> d = new HashMap<>();
                        s.entrySet().forEach(sn ->{
                            d.put(pre+sn.getKey()+"_0",sn.getValue());
                        });
                        list.add(d);
                    }
                }
            }else{
                //第一层 无 buckets
                if(StringUtils.isNotEmpty(key2) && ob.containsKey(key2)){
                    JSONObject ob2 = ob.getJSONObject(key2);
                    if(ob2.containsKey("buckets")){
                        JSONArray buckets2 = ob2.getJSONArray("buckets");
                        buckets2.forEach(e2 ->{
                            Map<String, Object> d = new HashMap<>();
                            JSONObject s2 = (JSONObject)e2;
                            ob.entrySet().forEach(sn ->{
                                d.put(pre+sn.getKey()+"_0",sn.getValue());
                            });
                            s2.entrySet().forEach(en ->{
                                d.put(pre+en.getKey()+"_1",en.getValue());
                            });
                            list.add(d);
                        });
                    }else{
                        Map<String, Object> d = new HashMap<>();
                        ob.entrySet().forEach(sn ->{
                            d.put(pre+sn.getKey()+"_0",sn.getValue());
                        });
                        ob2.entrySet().forEach(en ->{
                            d.put(pre+en.getKey()+"_1",en.getValue());
                        });
                        list.add(d);
                    }
                }else{
                    //不包含第二层
                    Map<String, Object> d = new HashMap<>();
                    ob.entrySet().forEach(sn ->{
                        d.put(pre+sn.getKey()+"_0",sn.getValue());
                    });
                    list.add(d);
                }
            }
        }
        log.info("####解析es聚合结果结束");
        log.info("####解析结果："+JSONArray.toJSONString(list));
        return list;
    }

    public static Integer parseStr2Int(String str){
        if(StringUtils.isEmpty(str) || "null".equals(str)){
            return 0;
        }else{
            return Integer.parseInt(str);
        }
    }
}
