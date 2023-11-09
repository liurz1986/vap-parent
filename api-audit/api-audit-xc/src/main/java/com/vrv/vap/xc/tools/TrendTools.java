package com.vrv.vap.xc.tools;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.BehaviorAnalysisModelMapper;
import com.vrv.vap.xc.model.LineModel;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.pojo.BehaviorAnalysisModel;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrendTools {
    private BehaviorAnalysisModelMapper behaviorAnalysisModelMapper = VapXcApplication.getApplicationContext().getBean(BehaviorAnalysisModelMapper.class);


    public static List<Map<String, Object>> buildAvgTrend(List<Map<String, Object>> source,int days){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> collect = source.stream().collect(Collectors.toMap(r -> r.get("date").toString(), r -> Integer.parseInt(r.get("count").toString())));
        for(int i = days; i > 0;i--){
            long total = 0;
            for(int j = i+days-1; j >= i; j--){
                int c = collect.get(DateTools.genDateStringBefore(j)) != null ? collect.get(DateTools.genDateStringBefore(j)) : 0;
                total += c;
            }
            long avg = total/days;
            Map<String,Object> record = new HashMap<>();
            record.put("date",DateTools.genDateStringBefore(i));
            record.put("count",avg);
            result.add(record);
        }
        return result;
    }

    public static List<Map<String, Object>> buildToalTrend(List<Map<String, Object>> source,int days){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> collect = source.stream().collect(Collectors.toMap(r -> r.get("date").toString(), r -> Integer.parseInt(r.get("count").toString())));
        for(int i = days; i > 0;i--){
            long total = 0;
            for(int j = i+days-1; j >= i; j--){
                int c = collect.get(DateTools.genDateStringBefore(j)) != null ? collect.get(DateTools.genDateStringBefore(j)) : 0;
                total += c;
            }
            Map<String,Object> record = new HashMap<>();
            record.put("date",DateTools.genDateStringBefore(i));
            record.put("count",total);
            result.add(record);
        }
        return result;
    }

    public static LineModel rendLineModel(PageModel srcmodel){
        LineModel model = new LineModel();
        model.setStartTime(TimeTools.format2(DateTools.getDateAfterByDay(srcmodel.getMyStartTime(),1)));
        model.setEndTime(TimeTools.format2(DateTools.getDateAfterByDay(srcmodel.getMyEndTime(),1)));
        return model;
    }

    public static void rendOrgDevRoleCondition(ObjectAnalyseModel record,LineModel query){
        StringBuffer where = new StringBuffer();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            where.append(" and user_no = '").append(record.getUserAcount()).append("'");
        }
        if (StringUtils.isNotEmpty(record.getUserType())) {
            where.append(" and std_user_type = '").append(record.getUserType()).append("'");
        }
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            where.append(" and std_is_same_unit = '").append(record.getIsLocalOrg()).append("'");
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                String[] split = record.getDeviceType().split(",");
                if(split.length > 0){
                    where.append(" and dst_std_dev_type_group in(");
                    for(int i = 0 ; i<split.length;i++){
                        if(i != 0){
                            where.append(",");
                        }
                        where.append("'");
                        where.append(split[i]);
                        where.append("'");
                    }
                    where.append(")");
                }
            } else {
                if ("3".equals(record.getDeviceType())) {
                    where.append(" and dst_std_sys_id <> ''");
                } else {
                    where.append(" and dst_std_dev_type_group ='").append(record.getDeviceType()).append("'");
                }
            }
        }
        query.setWhere(where.toString());
    }

    public void renderConfig(){
        QueryWrapper<BehaviorAnalysisModel> query = new QueryWrapper<BehaviorAnalysisModel>();
        List<BehaviorAnalysisModel> behaviorAnalysisModels = this.behaviorAnalysisModelMapper.selectList(query);
        for(BehaviorAnalysisModel model : behaviorAnalysisModels){
            String config = model.getConfig();
            JSONObject configObj = JSONObject.parseObject(config);
            JSONObject summary = configObj.getJSONObject("summary");
            JSONObject line = configObj.getJSONObject("line");
            boolean flag = false;
            if(summary == null){
                continue;
            }
            if("`count`".equals(summary.getString("fields"))){
                summary.put("fields","SUM(`count`) count");
                summary.put("group","data_time");
                flag = true;
            }
            if(line == null){
                line = new JSONObject();
                String where = summary.getString("where");
                line.put("fields","count_avg count");
                if(StringUtils.isNotEmpty(where)){
                    line.put("where",where);
                }
                flag = true;
            }
            if(flag){
                JSONObject con = new JSONObject();
                con.put("summary",summary);
                con.put("line",line);
                model.setConfig(JSONObject.toJSONString(con));
                behaviorAnalysisModelMapper.updateById(model);
            }
        }
    }

    public void renderLineConfig(){
        QueryWrapper<BehaviorAnalysisModel> query = new QueryWrapper<BehaviorAnalysisModel>();
        List<BehaviorAnalysisModel> behaviorAnalysisModels = this.behaviorAnalysisModelMapper.selectList(query);
        for(BehaviorAnalysisModel model : behaviorAnalysisModels) {
            String config = model.getConfig();
            JSONObject configObj = JSONObject.parseObject(config);
            JSONObject line = configObj.getJSONObject("line");
            JSONObject summary = configObj.getJSONObject("summary");
            if(line == null){
                continue;
            }
            if(summary == null){
                summary = JSONObject.parseObject(JSONObject.toJSONString(line));
                summary.put("fields","SUM(`count`) count");
                summary.put("group","data_time");
                JSONObject con = new JSONObject();
                con.put("summary",summary);
                con.put("line",line);
                model.setConfig(JSONObject.toJSONString(con));
                behaviorAnalysisModelMapper.updateById(model);
            }
        }
    }
}
