package com.vrv.vap.line.schedule.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.config.MessageConfig;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.tools.*;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.client.RedisCacheTools;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineResultMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.line.service.KafkaSenderService;
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

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class BaseLineTask extends BaseTask{

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
    private static final Log log = LogFactory.getLog(BaseLineTask.class);
    private String MESSAGE_TOPIC = "filter-data-baseline";
    private String FIELD_NAME = "暂无";
    private String SOURCE_NAME_END = "-*";

    @Override
    void run(String jobName) {

    }

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        int id =  jobDataMap.getInt("id");
        BaseLine line = baseLineMapper.selectById(id);
        log.info("基线任务执行开始id："+id);
        new LineTaskRun().runLine(line);
    }
}
