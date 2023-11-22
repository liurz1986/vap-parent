package com.vrv.rule.ruleInfo.udf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.util.JdbcSingeConnectionUtil;
import com.vrv.rule.util.PatternTools;
import com.vrv.rule.util.RedissonSingleUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.functions.ScalarFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 应用访问首访序列自定义函数
 */
public class AppVisitFunction extends ScalarFunction {


    private static Gson gson = new Gson();
    private static final long serialVersionUID = 1L;

    public AppVisitFunction() {

    }

    /**
     * 首访序列自定义函数操作
     *
     * @param obj
     * @param intervalTime
     * @param resCode
     * @param systemId
     * @return
     */
    public int eval(Object obj, int intervalTime, String resCode, String systemId) {
        String json = gson.toJson(obj);
        List<Map<String, Object>> gabObject = gson.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
        int location = getIndex(gabObject, intervalTime * 1000, "time_stamp");
        List<Map<String, Object>> firstVisitList = gabObject.subList(0, location+1);
        //过滤去掉firstVisitList当中，resCode当中包含resultCode
        List<Map<String, Object>> resultByResCode = getResultByResCode(resCode, firstVisitList);
        List<Map<String, Object>> appUrlBySystemId = getAppUrlBySystemId(systemId);
        //处理掉resultByResCode当中url的问题
        appUrlBySystemId = deakResultByUrlPreFix(appUrlBySystemId);
        //判断resultByResCode当中url是否在appUrlBySystemId当中
        boolean contain = isContain(resultByResCode, appUrlBySystemId);
        if (contain) {  //返回true表示包含白名单当中的数据
            return 1;
        } else {
            return 0;  //表示不在白名单，是需要异常输出的
        }
    }

    /**
     * 判断resultByResCode当中url是否在appUrlBySystemId当中
     *
     * @param resultByResCode
     * @param appUrlBySystemId
     * @return
     */
    public boolean isContain(List<Map<String, Object>> resultByResCode, List<Map<String, Object>> appUrlBySystemId) {
        if(resultByResCode.size()==0 || appUrlBySystemId.size()==0){
            return true; //当resultByResCode当中url为空，或者appUrlBySystemId当中url为空，都返回true
        }
        List<Map<String, Object>> result = resultByResCode.stream().filter(map -> {
            String url = map.getOrDefault("url", "").toString();
            if (StringUtils.isEmpty(url)) {
                return false;
            }
            boolean appUrl = appUrlBySystemId.stream().anyMatch(item -> item.get("app_url").toString().contains(url));
            boolean operationUrl = appUrlBySystemId.stream().anyMatch(item -> item.get("operation_url").toString().contains(url));
            return appUrl || operationUrl;
        }).collect(Collectors.toList());

        if (result.size() > 0) {  //包含白名单当中的数据，返回true
            return true;
        } else {
            return false;
        }

    }


    /**
     * 处理掉对象当中url前缀
     *
     * @param appUrlBySystemId
     * @return
     */
    public List<Map<String, Object>> deakResultByUrlPreFix(List<Map<String, Object>> appUrlBySystemId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : appUrlBySystemId) {
            String appUrl = (String) map.getOrDefault("app_url", "");
            String operationUrl = (String) map.getOrDefault("operation_url", "");
            String appUrlLast = PatternTools.patternCheak(appUrl); //应用系统的url
            String operationUrlLast = PatternTools.patternCheak(operationUrl); //应用系统的操作url
            Map<String, Object> element = new HashMap<>();
            element.put("app_url", appUrlLast);
            element.put("operation_url", operationUrlLast);
            result.add(element);
        }
        return result;
    }

    /**
     * 根据systemId获得app_urlh和operation_url
     *
     * @param systemId
     * @return
     */
    public List<Map<String, Object>> getAppUrlBySystemId(String systemId) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (RedissonSingleUtil.getInstance().exists(systemId)) {   //说明之前已经访问过了，直接从缓存当中读取数据
            String json = RedissonSingleUtil.getInstance().get(systemId);
            list = gson.fromJson(json, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
        } else {
            String sql = "select app_url,operation_url from app_sys_manager where app_no = ?";
            List<String> params = new ArrayList<>();
            params.add(systemId);
            list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql, params);
            RedissonSingleUtil.getInstance().set(systemId, gson.toJson(list));
        }
        return list;
    }

    public List<Map<String, Object>> getResultByResCode(String resCode, List<Map<String, Object>> firstVisitList) {
        List<Map<String, Object>> resultByResCode = firstVisitList.stream().filter(map -> {
            String resultCode = map.getOrDefault("http_res_code", "0").toString();
            if (resCode.contains(resultCode)) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        return resultByResCode;
    }


    /**
     * 给一个数组，判断数组相邻两个元素之间的值是否超过了指定的时间间隔,并返回超过阈值那个元素的下标
     *
     * @param list
     * @param intervalTime
     * @param elementName
     * @return
     */
    public int getIndex(List<Map<String, Object>> list, int intervalTime, String elementName) {
        int index = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            String timeStampBefore = list.get(i).getOrDefault(elementName, "0").toString();
            String timeStampAfter =  list.get(i + 1).getOrDefault(elementName, "0").toString();
            Long before = Long.valueOf(timeStampBefore);
            Long after = Long.valueOf(timeStampAfter);
            Long value = after - before;
            if (after - before > intervalTime) {
                index = i;
                break;
            }
        }
        return index;
    }


}
