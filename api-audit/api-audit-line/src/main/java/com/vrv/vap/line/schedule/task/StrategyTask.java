package com.vrv.vap.line.schedule.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.client.RedisCacheTools;
import com.vrv.vap.line.config.MessageConfig;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineResultMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.mapper.StrategyConfigMapper;
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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class StrategyTask extends BaseTask {

    private String SOURCE_NAME_END = "-*";

    @Override
    void run(String jobName) {

    }

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        int id = jobDataMap.getInt("id");
        new StrateyTools().run(id);
    }

}
