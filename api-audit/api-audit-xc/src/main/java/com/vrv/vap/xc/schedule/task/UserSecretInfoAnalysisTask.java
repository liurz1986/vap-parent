package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.DepartSecretInfoTotal;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.pojo.UserSecretInfoDay;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;
import com.vrv.vap.xc.service.*;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

import java.util.*;

/**
 * 用户涉密文件分析
 * Created by jiangcz on 2021/8/19
 */
public class UserSecretInfoAnalysisTask extends BaseTask {

    private UserSecretInfoTotalService totalService = VapXcApplication.getApplicationContext().getBean(UserSecretInfoTotalService.class);

    private UserSecretInfoDayService dayService = VapXcApplication.getApplicationContext().getBean(UserSecretInfoDayService.class);

    private DepartSecretInfoTotalService departService = VapXcApplication.getApplicationContext().getBean(DepartSecretInfoTotalService.class);

    // 索引
    //private final static String INDEX = "file-audit,print-audit";
    private final static String INDEX = "netflow-app-file";

    // 批量入库大小
    private final static int BATCH = 500;

    @Override
    void run(String jobName) {
        //
        Date now = new Date();
        log.info("task UserSecretInfoAnalysisTask start");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEX.split(","), "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        queryModel.setQueryBuilder(query);
        //List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel, wrapper, "dev_ip", "src_file_name", 9999, 9999, "count", "std_user_no,username,std_org_code,std_org_name,business_list".split(","));
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel, wrapper, "dst_std_dev_ip", "file_name", 9999, 9999, "count", "std_user_no,username,dst_std_org_code,dst_std_org_name,business_list".split(","));
        List<UserSecretInfoDay> list = new ArrayList<>();
        /*
        maps.forEach(e ->{
            //std_user_no,username,std_org_code,std_org_name,business_list
            UserSecretInfoDay info = new UserSecretInfoDay();
            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            info.setUserNo(StringValueOf(e.get("std_user_no")));
            info.setUserName(StringValueOf(e.get("username")));
            info.setDepartNo(StringValueOf(e.get("std_org_code")));
            info.setDepartName(StringValueOf(e.get("std_org_name")));
            info.setBusiness(StringValueOf(e.get("business_list")));
            info.setSecretFileName(StringValueOf(e.get("src_file_name")));
            info.setDeviceIp(StringValueOf(e.get("dev_ip")));
            info.setCount(parseCount(e.get("count")));
            list.add(info);
        });*/
        maps.forEach(e ->{
            //std_user_no,username,std_org_code,std_org_name,business_list
            UserSecretInfoDay info = new UserSecretInfoDay();
            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            info.setUserNo(StringValueOf(e.get("std_user_no")));
            info.setUserName(StringValueOf(e.get("username")));
            info.setDepartNo(StringValueOf(e.get("dst_std_org_code")));
            info.setDepartName(StringValueOf(e.get("dst_std_org_name")));
            info.setBusiness(StringValueOf(e.get("business_list")));
            info.setSecretFileName(StringValueOf(e.get("file_name")));
            info.setDeviceIp(StringValueOf(e.get("dst_std_dev_ip")));
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
        List<UserSecretInfoTotal> totals = dayService.countAll();

        // 历史统计数据更新
        if (totals != null && totals.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= totals.size() / BATCH; i++) {
                if (i == totals.size() / BATCH) {
                    if (totals.subList(i * BATCH, totals.size()).size() > 0) {
                        totalService.replaceInto(totals.subList(i * BATCH, totals.size()));
                    }
                } else {
                    totalService.replaceInto(totals.subList(i * BATCH, (i + 1) * BATCH));
                }
            }
        }

        //统计部门数据
        List<Map<String, String>> datas = dayService.countByDepart();
        datas.forEach(e ->{
            DepartSecretInfoTotal t = new DepartSecretInfoTotal();
            t.setDepartNo(e.get("departNo"));
            t.setDepartName(e.get("departName"));
            t.setSecretFileNum(parseCount(e.get("num")));
            t.setSecretFileCount(parseCount(e.get("count")));
            Set<String> size = new HashSet<>();
            String[] businesses = e.get("business").split(",");
            Collections.addAll(size,businesses);
            t.setBusinessCount(size.size());
            t.setTime(now);
            DepartSecretInfoTotal old = departService.getById(t.getDepartNo());
            if(old == null){
                //新增
                departService.save(t);
            }else{
                //更新原数据累加
                old.setSecretFileCount(old.getSecretFileCount()+t.getSecretFileCount());
                old.setSecretFileNum(old.getSecretFileNum()+t.getSecretFileNum());
                old.setBusinessCount(old.getBusinessCount()+t.getBusinessCount());
                old.setTime(now);
                departService.updateById(old);
            }
        });
        log.info("task UserSecretInfoAnalysisTask end");
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
