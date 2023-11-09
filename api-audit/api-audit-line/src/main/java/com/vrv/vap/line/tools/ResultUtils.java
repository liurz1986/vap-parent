package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ResultUtils {
    public ResultUtils() {
    }

    private static List<Map<String, Object>> getBuckets(String key, Map<String, Object> data,String column) {
        Object obj = data.get(key);
        //if()
        List<Map<String, Object>> result = obj instanceof Map ? (List)((Map)obj).get("buckets") : null;
        if("无".equals(column) && CollectionUtils.isEmpty(result)){
            result = new ArrayList<>();
            result.add(data);
        }
        return result;
    }

    private static String[] getAkey(Map<String, Object> data) {
        Iterator<String> it = data.keySet().iterator();
        String key = null;

        do {
            if (!it.hasNext()) {
                return null;
            }

            key = (String)it.next();
        } while(!key.contains("#"));

        return key.split("#");
    }

    private static Map<String, Object> getSampleData(String prefix, Map<String, Object> aggData) {
        String[] akey = getAkey(aggData);
        if (null != akey && "data".equals(akey[1])) {
            Object a = aggData.get(akey[0] + "#" + akey[1]);
            if (a instanceof JSONObject) {
                JSONObject o = (JSONObject)a;
                JSONObject h = o.getJSONObject("hits");
                if (null != h) {
                    JSONArray hs = h.getJSONArray("hits");
                    if (null != hs) {
                        Map<String, Object> resultMap = new HashMap();

                        for(int i = 0; i < hs.size(); ++i) {
                            JSONObject hsmo = hs.getJSONObject(i);
                            resultMap.put(prefix + "__index", hsmo.getString("_index"));
                            JSONObject source = hsmo.getJSONObject("_source");
                            if (null != source) {
                                String idx = String.valueOf(i);
                                source.forEach((k, v) -> {
                                    resultMap.put(String.join("_", prefix, k, idx), v);
                                });
                            }
                        }

                        return resultMap;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }
/*
    public static Map<String, Object> spreadAggregationAsMap(Result result) {
        Map<String, Object> rootMap = new TreeMap();
        Map<String, Object> aggMap = result.getAggregations();
        spreadMap(rootMap, aggMap, (String)null);
        return rootMap;
    }*/

    public static String getKey(Map<String, Object> aggMap){
        Set<String> keys = aggMap.keySet();
        Object[] ars = keys.toArray();
        return ars[0].toString();
    }

    public static List<Map<String, Object>> spreadAggregationAsList(Map<String, Object> aggMap,String column) {
        List<Map<String, Object>> rootList = new ArrayList();
        List<Map<String, Object>> root = new ArrayList();
        List<Map<String, Object>> bucketList = getBuckets(getKey(aggMap), aggMap,column);
        if (null != bucketList) {
            bucketList.forEach((m) -> {
                Map<String, Object> tmp = new TreeMap();
                spreadMap(tmp, sortMapByType(m), (String)null,rootList);
                rootList.add(tmp);
            });
        }
        root = rootList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<Map<String, Object>>(
                                Comparator.comparing(m -> JSON.toJSONString(sortMapByKey(m))))), ArrayList::new)
        );
        return root;
    }

    private static void spreadMap(Map<String, Object> rootMap, Map<String, Object> aggMap, String rootPrefix,List<Map<String, Object>> rootList) {
        String rootPrefix2 = StringUtils.isEmpty(rootPrefix) ? "" : rootPrefix + "_";
        aggMap.forEach((k, v) -> {
            int idx = k.indexOf("#");
            String prefix = rootPrefix2 + (-1 == idx ? k : k.substring(idx + 1));
            if (v instanceof List) {
                spreadList(rootMap, (List)v, prefix,rootList);
            } else if (v instanceof Map) {
                spreadMap(rootMap, sortMapByType((Map)v), prefix,rootList);
            } else {
                rootMap.put(prefix, v);
            }

        });
    }

    private static void spreadList(Map<String, Object> rootMap, List<Object> aggList, String rootPrefix,List<Map<String, Object>> rootList) {
        String prefix = null;
        Object value = null;
        for(int i = 0; i < aggList.size(); ++i) {
            //prefix = rootPrefix + "_" + i;
            Map<String, Object> itmMap = new HashMap<>();
            prefix = rootPrefix;
            value = aggList.get(i);
            if (value instanceof Map) {
                spreadMap(rootMap, sortMapByType((Map)value), prefix,rootList);
            } else if (value instanceof List) {
                spreadList(rootMap, (List)value, prefix,rootList);
            } else {
                itmMap.put(prefix, value);
            }
            itmMap.putAll(rootMap);
            rootList.add(itmMap);
        }

    }

    public static Map<String,Object> sortMapByKey(Map<String,Object> map){
        LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
        map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;

    }

    public static Map<String,Object> sortMapByType(Map<String,Object> map){
        Comparator<Map.Entry<String, Object>> valCmp = new Comparator<Map.Entry<String,Object>>() {
            @Override
            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                return type2Int(o2)-type2Int(o1);  // 降序排序，如果想升序就反过来
            }
        };
        List<Map.Entry<String, Object>> list = new ArrayList<Map.Entry<String,Object>>(map.entrySet());
        Collections.sort(list,valCmp);
        LinkedHashMap<String, Object> result = Maps.newLinkedHashMap();
        list.forEach(e ->{
            result.put(e.getKey(),e.getValue());
        });
        return result;
    }

    public static int type2Int(Map.Entry<String, Object> entry){
        int i = 2;
        if("top".equals(entry.getKey())){
            return 1;
        }
        if(entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Double || entry.getValue() instanceof Float){
            i = 4;
        }else if(entry.getValue() instanceof Map){
            i = 3;
        }else if(entry.getValue() instanceof Collection){
            i = 2;
        }
        return i;
    }

/*
    public static void main(String[] args) {
        String s = "{\"terms_user_no\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"005\",\"doc_count\":1,\"terms_file_type\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"doc\",\"doc_count\":1,\"sum_count\":{\"value\":2099204},\"top\":{\"hits\":{\"total\":{\"value\":1,\"relation\":\"eq\"},\"max_score\":1,\"hits\":[{\"_index\":\"base-line-print-file-type-process-2022\",\"_type\":\"_doc\",\"_id\":\"zVWyv38Bm7BdculpHDc3\",\"_score\":1,\"_source\":{\"file_type\":\"doc\",\"count\":2099204,\"insert_time\":\"2022-03-25 14:09:30\",\"user_no\":\"005\",\"username\":\"白建屏\"}}]}}}]}},{\"key\":\"006\",\"doc_count\":1,\"terms_file_type\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"doc\",\"doc_count\":1,\"sum_count\":{\"value\":2099177},\"top\":{\"hits\":{\"total\":{\"value\":1,\"relation\":\"eq\"},\"max_score\":1,\"hits\":[{\"_index\":\"base-line-print-file-type-process-2022\",\"_type\":\"_doc\",\"_id\":\"zFWyv38Bm7BdculpHDc3\",\"_score\":1,\"_source\":{\"file_type\":\"doc\",\"count\":2099177,\"insert_time\":\"2022-03-25 14:09:30\",\"user_no\":\"006\",\"username\":\"蔡巍\"}}]}}}]}}]}}";
        Map<String,Object> mapTypes = JSON.parseObject(s);
        List<Map<String, Object>> list = spreadAggregationAsList(mapTypes);
        list.forEach(e ->{
            System.out.println(e.toString());
        });
    }*/
}