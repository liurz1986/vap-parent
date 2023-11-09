package com.vrv.vap.line.schedule.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.client.RedisCacheTools;
import com.vrv.vap.line.config.MessageConfig;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineResultMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
//import com.vrv.vap.line.mapper.LineTimeModelMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.line.service.KafkaSenderService;
import com.vrv.vap.line.tools.*;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.joda.time.DateTimeZone;
import org.quartz.JobDataMap;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class LineResultTask extends BaseTask{

    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    //private BaseLineSourceMapper baseLineSourceMapper = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceMapper.class);
    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    //private BaseLineSourceFieldService baseLineSourceFieldService = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceFieldService.class);
    private KafkaSenderService kafkaSenderService = VapLineApplication.getApplicationContext().getBean(KafkaSenderService.class);
    private RedisCacheTools redisCacheTools = VapLineApplication.getApplicationContext().getBean(RedisCacheTools.class);
    private BaseLineResultMapper baseLineResultMapper = VapLineApplication.getApplicationContext().getBean(BaseLineResultMapper.class);
    private ApiDataClient apiDataClient = VapLineApplication.getApplicationContext().getBean(ApiDataClient.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private MessageConfig messageConfig = VapLineApplication.getApplicationContext().getBean(MessageConfig.class);
//    private LineTimeModelMapper lineTimeModelMapper = VapLineApplication.getApplicationContext().getBean(LineTimeModelMapper.class);
    private String PRE_CN = "_";
    private String PRE_SM = LineConstants.NAME_PRE.PRE_SM;
    private String PRE_LINE = LineConstants.NAME_PRE.PRE_LINE;
    private String TIME_FIELD = "insert_time";
    private final String indexSufFormat = "-yyyy";
    private static final Log log = LogFactory.getLog(LineResultTask.class);
    private String MESSAGE_TOPIC = "filter-data-baseline";
    private String FIELD_NAME = "暂无";
    private String SOURCE_NAME_END = "-*";

    @Override
    void run(String jobName) {
        log.info("基线重跑任务开始执行");
        QueryWrapper<BaseLineResult> wrapper = new QueryWrapper<>();
        wrapper.eq("result",LineConstants.LINE_RESULT.FAILED);
        List<BaseLineResult> baseLineResults = baseLineResultMapper.selectList(wrapper);
        List<BaseLine> baseLines = baseLineMapper.selectList(new QueryWrapper<>());
        Map<Integer, BaseLine> collect = baseLines.stream().collect(Collectors.toMap(r -> r.getId(), r -> r));
        LineTaskRun run = new LineTaskRun();
        for(BaseLineResult result : baseLineResults){
            String config = result.getConfig();
            if(StringUtils.isNotEmpty(config)){
                JSONObject jsonObject = JSONObject.parseObject(config);
                Date startTime = TimeTools.parseDate(jsonObject.getString("startTime"), "yyyy-MM-dd HH:mm:ss SSS");
                Date endTime = TimeTools.parseDate(jsonObject.getString("endTime"),"yyyy-MM-dd HH:mm:ss SSS");
                log.info("重跑基线："+JSONObject.toJSONString(result));
                run.runByTime(collect.get(result.getBaseLineId()),result,jsonObject.getIntValue("summaryNum"),startTime,endTime);
            }
        }
    }

}
