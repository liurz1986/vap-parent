package com.flink.demo.offline;


import com.google.gson.Gson;
import com.vrv.rule.source.datasourceconnector.es.ElasticSearchDataSourceRunner;
import com.vrv.rule.source.datasourceconnector.es.util.QueryCondition_ES;
import com.vrv.rule.source.datasourceconnector.mysql.MysqlDataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.StartConfig;
import com.vrv.rule.source.datasourceparam.impl.MysqlRunnerParams;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.TimeSelectVO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OfflineTaskTest {

    private StartConfig startConfig;

    private Gson gson = new Gson();

    @Before
    public void ConstrutStartUpConfig(){
        List<StartConfig.BusinessSelectField> businessSelectFields = getBusinessSelectFields();
        StartConfig.TimeSelectCondition timeSelectCondition = getTimeSelectCondition();
        StartConfig.CycleStrategyCondition cycleStrategyCondition = getCycleStrategyCondition();
        startConfig = StartConfig.builder()
                                 .businessSelectFields(businessSelectFields)
                                 .timeSelectCondition(timeSelectCondition)
                                 .cycleStrategyCondition(cycleStrategyCondition).build();
    }

    private List<StartConfig.BusinessSelectField> getBusinessSelectFields(){
        List<StartConfig.BusinessSelectField> list = new ArrayList<>();
        StartConfig.BusinessSelectField businessSelectField = new StartConfig.BusinessSelectField();
        businessSelectField.setSelectField("type");
        businessSelectField.setRelationship("isNull");
        businessSelectField.setThreshhold("fefef");
        businessSelectField.setSelectFieldType("varchar");
        list.add(businessSelectField);
        return list;
    }

    private StartConfig.TimeSelectCondition getTimeSelectCondition(){

        StartConfig.TimeSelectCondition timeSelectCondition1 = new StartConfig.TimeSelectCondition();
        timeSelectCondition1.setTimeField("time");
        timeSelectCondition1.setType("day");
        return timeSelectCondition1;
    }

    private StartConfig.CycleStrategyCondition getCycleStrategyCondition(){
        StartConfig.CycleStrategyCondition cycleStrategyCondition = new StartConfig.CycleStrategyCondition();
        cycleStrategyCondition.setType("hour");
        cycleStrategyCondition.setCronExpression("0 0 0/1 * * ?");
        return cycleStrategyCondition;
    }

    private List<FieldInfoVO> getFieldInfoVOs(){
        List<FieldInfoVO> fieldInfoVOs = new ArrayList<>();
        FieldInfoVO fieldInfoVO1 = new FieldInfoVO();
        fieldInfoVO1.setFieldName("type_");
        fieldInfoVO1.setFieldType("varchar");


        FieldInfoVO fieldInfoVO2 = new FieldInfoVO();
        fieldInfoVO2.setFieldName("name_");
        fieldInfoVO2.setFieldType("varchar");


        fieldInfoVOs.add(fieldInfoVO1);
        fieldInfoVOs.add(fieldInfoVO2);

        return fieldInfoVOs;
    }



    @Test
    public void testConstructOfflineVO(){
       Gson gson  = new Gson();
       String json = gson.toJson(startConfig);

        StartConfig startConfig = gson.fromJson(json, StartConfig.class);
        System.out.println(startConfig);
        System.out.println(json);
    }

    @Test
    public void testTimeVO(){
        StartConfig.TimeSelectCondition timeSelectCondition = startConfig.getTimeSelectCondition();
        TimeSelectVO timeSelectVO = FilterOperatorUtil.getTimeSelectVO(timeSelectCondition);
        String timeJson = gson.toJson(timeSelectVO);
        System.out.println(timeJson);
    }

    /**
     * 构造msql的sql语句
     */
    @Test
    public void constructOfflineSql(){
        MysqlDataStreamSourceRunner mysqlDataStreamSourceRunner = new MysqlDataStreamSourceRunner();
        String json = gson.toJson(startConfig);
        MysqlRunnerParams mysqlRunnerParams = MysqlRunnerParams.builder().tableName("sql_test").startConfig(json).build();
        mysqlRunnerParams.setFieldInfoVOs(getFieldInfoVOs());
        String dataSourceStreamSql = mysqlDataStreamSourceRunner.getDataSourceStreamSql(mysqlRunnerParams);
        System.out.println(dataSourceStreamSql);
    }

    @Test
    public void constructOfflineEsCondition(){
        ElasticSearchDataSourceRunner elasticSearchDataSourceRunner = new ElasticSearchDataSourceRunner();
        List<QueryCondition_ES> selectConditions = elasticSearchDataSourceRunner.getSelectConditions(startConfig);
        System.out.println(selectConditions);

    }




}
