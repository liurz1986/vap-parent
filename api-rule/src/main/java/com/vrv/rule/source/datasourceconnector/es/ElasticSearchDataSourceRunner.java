package com.vrv.rule.source.datasourceconnector.es;

import com.google.gson.Gson;
import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import com.vrv.rule.source.datasourceparam.StartConfig;
import com.vrv.rule.source.datasourceparam.impl.ESRunnerParams;
import com.vrv.rule.source.datasourceparam.impl.EsDatasourceParam;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.util.JasyptUtil;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.TimeSelectVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;



import java.util.List;

/**
 * @description: ElasticSearch数据源启动类
 */
public class ElasticSearchDataSourceRunner implements DataStreamSourceRunner<ESRunnerParams> {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchDataSourceRunner.class);

    private Gson gson = new Gson();


    private static Long time = 60L;

    private static Integer size = 30;


    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, ESRunnerParams params) {

        //TODO 确定环境变量
        String esClusterHost = YmlUtil.getValue("application.yml", "ES_CLUSTER_HOST").toString();
        String esClusterPORT = YmlUtil.getValue("application.yml", "ELASTICSEARCH_PORT").toString();
        String hosts = esClusterHost + ":" + esClusterPORT;
        String esClusterPwd = YmlUtil.getValue("application.yml", "ES_CLUSTER_PWD").toString();
        esClusterPwd  = JasyptUtil.getDecryptPassword(esClusterPwd, "salt");
        Integer esSize = Integer.valueOf(YmlUtil.getValue("application.yml", "ES_SIZE").toString());
        String esClusterUserName = YmlUtil.getValue("application.yml", "ES_CLUSTER_UNAME").toString();

        String indexName = params.getIndexName();
        String startConfig = params.getStartConfig();
        StartConfig startConfigObj = gson.fromJson(startConfig, StartConfig.class);
        List<QueryCondition_ES> selectConditions = getSelectConditions(startConfigObj);


        //TODO 排序采用时间字段的升序排序
        String key = startConfigObj.getTimeSelectCondition().getTimeField();
        String sort = "asc";  //按照升序排列

        EsDatasourceParam esDatasourceParam = EsDatasourceParam.builder().conditions(selectConditions)
                .hostArrays(hosts).userName(esClusterUserName)
                .indexName(indexName).password(esClusterPwd)
                .size(esSize).sort(sort).key(key).time(time).build();

        esDatasourceParam.setFieldInfoVOs(params.getFieldInfoVOs());
        DataSourceConnector<EsDatasourceParam> elasticSearchDataSourceConnector = new ElasticSearchDataSourceConnector();
        DataStream<Row> dataStreamSource = elasticSearchDataSourceConnector.getDataStreamSource(env, esDatasourceParam);
        return dataStreamSource;
    }

    public  List<QueryCondition_ES> getSelectConditions(StartConfig startConfigObj) {
        List<QueryCondition_ES> conditons = new ArrayList<>();
        addTimeCondition(startConfigObj, conditons);
        addBusinessCondition(startConfigObj, conditons);
        return conditons;
    }

    /**
     * 添加业务筛选条件
     * @param startConfigObj
     * @param conditons
     */
    private static void addBusinessCondition(StartConfig startConfigObj, List<QueryCondition_ES> conditons) {
        List<StartConfig.BusinessSelectField> businessSelectFields = startConfigObj.getBusinessSelectFields();
        if (businessSelectFields != null && businessSelectFields.size() > 0) {
            for (StartConfig.BusinessSelectField businessSelectField : businessSelectFields) {
                String fieldName = businessSelectField.getSelectField();
                String operator = businessSelectField.getRelationship();
                String value = businessSelectField.getThreshhold();
                switch (operator) {
                    case "=":
                        conditons.add(QueryCondition_ES.eq(fieldName, value));
                        break;
                    case "!=":
                        conditons.add(QueryCondition_ES.not(QueryCondition_ES.eq(fieldName, value)));
                        break;
                    case ">":
                        conditons.add(QueryCondition_ES.ge(fieldName, Long.valueOf(value)));
                        break;
                    case "<":
                        conditons.add(QueryCondition_ES.lt(fieldName, Long.valueOf(value)));
                        break;
                    case "like":
                        conditons.add(QueryCondition_ES.like(fieldName, value));
                        break;
                    case "not like":
                        conditons.add(QueryCondition_ES.not(QueryCondition_ES.like(fieldName, value)));
                        break;
                    case "between":
                        String[] split = value.split(",");
                        conditons.add(QueryCondition_ES.between(fieldName, Long.valueOf(split[0]), Long.valueOf(split[1])));
                        break;
                    case "not between":
                        String[] notsplit = value.split(",");
                        conditons.add(QueryCondition_ES.not(QueryCondition_ES.between(fieldName, Long.valueOf(notsplit[0]), Long.valueOf(notsplit[1]))));
                        break;
                    case "in":
                        conditons.add(QueryCondition_ES.in(fieldName, value.split(",")));
                        break;
                    case "not in":
                        conditons.add(QueryCondition_ES.not(QueryCondition_ES.in(fieldName, value.split(","))));
                        break;
                    case "isNull":
                        conditons.add(QueryCondition_ES.isNull(fieldName));
                        break;
                    case "isNotNull":
                        conditons.add(QueryCondition_ES.not(QueryCondition_ES.isNull(fieldName)));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 增加时间筛选字段
     * @param startConfigObj
     * @param conditons
     */
    private static void addTimeCondition(StartConfig startConfigObj, List<QueryCondition_ES> conditons) {
        StartConfig.TimeSelectCondition timeSelectCondition = startConfigObj.getTimeSelectCondition();
        TimeSelectVO timeSelectVO = FilterOperatorUtil.getTimeSelectVO(timeSelectCondition);
        String timeField = timeSelectCondition.getTimeField();
        String startTime = timeSelectVO.getStartTime();
        String endTime = timeSelectVO.getEndTime();
        startTime = DateUtil.parseDefaultFormatToUTC(startTime); //转换为UTC时间
        endTime = DateUtil.parseDefaultFormatToUTC(endTime);
        conditons.add(QueryCondition_ES.between(timeField, startTime, endTime));
    }
}
