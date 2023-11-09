package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.pojo.QueryDuration;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PollingTimeTask extends BaseTask{
    @Override
    void run(String jobName) {
        Date now = new Date();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel pageModel = new PageModel();
        pageModel.setOrder("event_time");
        pageModel.setBy("asc");
        pageModel.setMyStartTime(TimeTools.getNowBeforeByDay(1));
        pageModel.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, new PageModel(), "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.mustNot(QueryBuilders.termQuery("report_dev_ip", ""));
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "report_dev_ip", 10000, "ip", "count");
        for(Map<String, Object> m : list){
            BoolQueryBuilder itemQuery = new BoolQueryBuilder();
            itemQuery.must(QueryBuilders.termQuery("report_dev_ip",  m.get("ip").toString()));
            queryModel.setQueryBuilder(itemQuery);
            queryModel.setCount(10000);
            SearchResponse searchResponse = null;
            List<Map<String, String>> allDate = new ArrayList<>();
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> datas = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                allDate.addAll(datas);
            }
            List<QueryDuration> records = new ArrayList<>();
            //计算时长入库
            if(CollectionUtils.isNotEmpty(allDate)){
                for(int i = 1; i <= allDate.size();i++ ){
                    String time = allDate.get(i).get("event_time");
                    String preTime = allDate.get(i-1).get("event_time");
                    //计算时长
                    Date date = TimeTools.parseDate(time, TimeTools.TIME_FMT_1);
                    Date preDate = TimeTools.parseDate(preTime, TimeTools.TIME_FMT_1);
                    long duration = (date.getTime()-preDate.getTime())/(1000*60);
                    QueryDuration record = new QueryDuration();
                    record.setDevId(allDate.get(i).get("report_dev_id"));
                    record.setDevIp(m.get("ip").toString());
                    record.setDuration(duration);
                    record.setQueryTime(date);
                    record.setInsertTime(now);
                    records.add(record);
                }
            }
            //入库
            if(CollectionUtils.isNotEmpty(records)){
                //todo
            }
        }
    }
}
