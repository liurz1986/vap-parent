package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.DataCleanMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.service.RptUserLoginHisService;
import com.vrv.vap.xc.service.RptUserLoginService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户登录终端分析
 * Created by lizj on 2021/8/19
 */
public class UserLoginTerminalAnalysisTask extends BaseTask {

    private RptUserLoginHisService rptUserLoginHisService = VapXcApplication.getApplicationContext().getBean(RptUserLoginHisService.class);

    private RptUserLoginService rptUserLoginService = VapXcApplication.getApplicationContext().getBean(RptUserLoginService.class);

    // 索引
    private final static String INDEX = "terminal-login";

    // 批量入库大小
    private final static int BATCH = 500;

    @Override
    void run(String jobName) {
        // 统计前一天终端登录日志（过滤操作类型为登录且操作结果为成功的数据）
        // op_type  操作类型  0-登录、1-退出
        // op_result  操作结果  0-成功、1-失败
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEX, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        query.must(QueryBuilders.termQuery("op_result", "0"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAggAndDateAgg(queryModel, wrapper, "key_id", "dev_ip", 9999, 9999, "count", "event_time", "HH", DateHistogramInterval.HOUR, 8, true);

        if (list != null && list.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= list.size() / BATCH; i++) {
                if (i == list.size() / BATCH) {
                    if (list.subList(i * BATCH, list.size()).size() > 0) {
                        rptUserLoginHisService.saveBatch4List(list.subList(i * BATCH, list.size()));
                    }
                } else {
                    rptUserLoginHisService.saveBatch4List(list.subList(i * BATCH, (i + 1) * BATCH));
                }
            }
        }

        // 结合历史统计数据汇总
        List<RptUserLogin> result = rptUserLoginHisService.countAll();
        // 历史统计数据更新
        if (result != null && result.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= result.size() / BATCH; i++) {
                if (i == result.size() / BATCH) {
                    if (result.subList(i * BATCH, result.size()).size() > 0) {
                        rptUserLoginService.replaceInto(result.subList(i * BATCH, result.size()));
                    }
                } else {
                    rptUserLoginService.replaceInto(result.subList(i * BATCH, (i+1)*BATCH));
                }
            }
        }
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }
}
