package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.DepartVisitAppTotalMapper;
import com.vrv.vap.xc.mapper.core.UserVisitAppDayMapper;
import com.vrv.vap.xc.mapper.core.UserVisitAppTotalMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.*;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 访问应用分析
 * Created by jiangcz on 2021/8/19
 */
public class UserVisitInfoAnalysisTask extends BaseTask {

    private UserVisitAppDayMapper dayService = VapXcApplication.getApplicationContext().getBean(UserVisitAppDayMapper.class);

    private UserVisitAppTotalMapper totalService = VapXcApplication.getApplicationContext().getBean(UserVisitAppTotalMapper.class);

    private DepartVisitAppTotalMapper departService = VapXcApplication.getApplicationContext().getBean(DepartVisitAppTotalMapper.class);

    // 索引
    //private final static String INDEX = "file-audit,print-audit";
    private final static String INDEX = "netflow-http";

    // 批量入库大小
    private final static int BATCH = 500;

    @Override
    void run(String jobName) {
        log.info("task UserVisitInfoAnalysisTask start");
        // 统计前一天用户访问应用数据
        Date now = new Date();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEX.split(","), "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        queryModel.setQueryBuilder(query);
        //List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel, wrapper, "dev_ip", "std_sys_id", 9999, 9999, "count", "std_user_no,username,std_org_code,std_org_name,app_name".split(","));
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel, wrapper, "dst_std_dev_ip", "dst_std_sys_id", 9999, 9999, "count", "std_user_no,username,dst_std_org_code,dst_std_org_name,app_name".split(","));
        List<UserVisitAppDay> list = new ArrayList<>();
        /*maps.forEach(e ->{
            //std_user_no,username,std_org_code,std_org_name,business_list
            UserVisitAppDay info = new UserVisitAppDay();
            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            info.setUserNo(StringValueOf(e.get("std_user_no")));
            info.setUserName(StringValueOf(e.get("username")));
            info.setDepartNo(StringValueOf(e.get("std_org_code")));
            info.setDepartName(StringValueOf(e.get("std_org_name")));
            info.setDeviceIp(StringValueOf(e.get("dev_ip")));
            info.setAppName(StringValueOf(e.get("app_name")));
            info.setAppNo(StringValueOf(e.get("std_sys_id")));
            info.setCount(parseCount(e.get("count")));
            list.add(info);
        });*/

        maps.forEach(e ->{
            //std_user_no,username,std_org_code,std_org_name,business_list
            UserVisitAppDay info = new UserVisitAppDay();
            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            info.setUserNo(StringValueOf(e.get("std_user_no")));
            info.setUserName(StringValueOf(e.get("username")));
            info.setDepartNo(StringValueOf(e.get("dst_std_org_code")));
            info.setDepartName(StringValueOf(e.get("dst_std_org_name")));
            info.setDeviceIp(StringValueOf(e.get("dst_std_dev_ip")));
            info.setAppName(StringValueOf(e.get("app_name")));
            info.setAppNo(StringValueOf(e.get("dst_std_sys_id")));
            info.setCount(parseCount(e.get("count")));
            list.add(info);
        });
        if (list != null && list.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= list.size() / BATCH; i++) {
                if (i == list.size() / BATCH) {
                    if (list.subList(i * BATCH, list.size()).size() > 0) {
                        dayService.saveBatch4List(list.subList(i * BATCH, list.size()));
                    }
                } else {
                    dayService.saveBatch4List(list.subList(i * BATCH, (i + 1) * BATCH));
                }
            }
        }
        // 结合历史统计数据汇总
        List<UserVisitAppTotal> totals = dayService.countAll();
        List<UserVisitAppTotal> totalrecords = new ArrayList<>();
        totals.forEach(e ->{
            UserVisitAppTotal a = new UserVisitAppTotal();
            BeanUtils.copyProperties(e, a);
            totalrecords.add(a);
        });
        // 历史统计数据更新
        if (totalrecords != null && totalrecords.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= totalrecords.size() / BATCH; i++) {
                if (i == totalrecords.size() / BATCH) {
                    if (totalrecords.subList(i * BATCH, totalrecords.size()).size() > 0) {
                        totalService.replaceInto(totalrecords.subList(i * BATCH, totalrecords.size()));
                    }
                } else {
                    totalService.replaceInto(totalrecords.subList(i * BATCH, (i+1) * BATCH));
                }
            }
        }
        //统计部门数据
        List<DepartVisitAppTotal> depars = dayService.countDepart();
        List<DepartVisitAppTotal> deparrecords = new ArrayList<>();
        depars.forEach( e ->{
            DepartVisitAppTotal a = new DepartVisitAppTotal();
            BeanUtils.copyProperties(e, a);
            deparrecords.add(a);
        });
        // 部门历史统计数据更新
        if (deparrecords != null && deparrecords.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= deparrecords.size() / BATCH; i++) {
                if (i == deparrecords.size() / BATCH) {
                    if (deparrecords.subList(i * BATCH, deparrecords.size()).size() > 0) {
                        departService.replaceInto(deparrecords.subList(i * BATCH, deparrecords.size()));
                    }
                } else {
                    departService.replaceInto(deparrecords.subList(i * BATCH, (i+1) * BATCH));
                }
            }
        }
        log.info("task UserVisitInfoAnalysisTask end");
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String[] indexs, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        List<String> allIndexList = new ArrayList<>();
        if (indexs.length > 0) {
            for (String index : indexs) {
                allIndexList.addAll(wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime()));
            }
        }
        if (!allIndexList.isEmpty()) {
            queryModel.setIndexNames(allIndexList.toArray(new String[allIndexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private String StringValueOf(Object o){
        return o != null ? o.toString() : "";
    }

    private Long parseCount(Object o){
        if(o == null){
            return 0L;
        }
        return Long.parseLong(o.toString());
    }

}
