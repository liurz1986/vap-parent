package com.vrv.rule.source.datasourceconnector.mysql;

import com.google.gson.Gson;
import com.vrv.rule.source.datasourceconnector.DataSourceConnector;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.StartConfig;
import com.vrv.rule.source.datasourceparam.impl.MysqlDatasourceParam;
import com.vrv.rule.source.datasourceparam.impl.MysqlRunnerParams;
import com.vrv.rule.util.ArrayUtil;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.TimeSelectVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MysqlDataStreamSourceRunner implements DataStreamSourceRunner<MysqlRunnerParams> {

    private static Logger logger = LoggerFactory.getLogger(MysqlDataStreamSourceRunner.class);

    private Gson gson = new Gson();

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";


    private static final Integer minimumIdel = 5;

    private static final Integer maximumPoolSize = 10;

    private static final Long idleTimeout = 30000L;



    @Override
    public DataStream<Row> getDataStreamSource(StreamExecutionEnvironment env, MysqlRunnerParams params) {
        String sql = getDataSourceStreamSql(params);
        String userName = YmlUtil.getValue("application.yml", "MYSQL_USER").toString();
        String password = YmlUtil.getValue("application.yml", "MYSQL_PASSWORD").toString();
        String jdbcUrl = getJdbcUrl();
        MysqlDatasourceParam mysqlDatasourceParam = MysqlDatasourceParam.builder().driverClassName(DRIVER_CLASS_NAME).sql(sql).jdbcUrl(jdbcUrl)
                                                    .userName(userName).password(password)
                                                    .minimumIdel(minimumIdel).maximumPoolSize(maximumPoolSize)
                                                    .idleTimeout(idleTimeout).build();
        mysqlDatasourceParam.setFieldInfoVOs(params.getFieldInfoVOs());
        DataSourceConnector<MysqlDatasourceParam> dataSourceConnector = new MysqlDataSourceConnector();
        DataStream<Row> dataStreamSource = dataSourceConnector.getDataStreamSource(env, mysqlDatasourceParam);
        return dataStreamSource;
    }

    /**
     * 获得JdbcUrl
     * @return
     */
    private static String getJdbcUrl() {
        String mysqlHost = YmlUtil.getValue("application.yml", "MYSQL_HOST").toString();
        String mysqlPort = YmlUtil.getValue("application.yml", "MYSQL_PORT").toString();
        String mysqlDbname = YmlUtil.getValue("application.yml", "MYSQL_DBNAME").toString();
        String jdbcUrl = "jdbc:mysql://"+mysqlHost+":"+mysqlPort+"/"+mysqlDbname+"?useSSL=false";
        logger.info("jdbcUrl:{}",jdbcUrl);
        return jdbcUrl;
    }

    /**
     * 获得数据源sql单元测试
     * @param params
     * @return
     */
    public String getDataSourceStreamSql(MysqlRunnerParams params) {
        String startConfig = params.getStartConfig();
        StartConfig startConfigObj = gson.fromJson(startConfig, StartConfig.class);
        String tableName = params.getTableName();
        String businessConditions = getBusinessConditions(startConfigObj);
        String selectFields = getSelectFields(params);
        StringBuffer sqlbuffer = new StringBuffer();
        sqlbuffer.append("select").append(" ").append(selectFields).append(" from ").append(tableName).append(" ")
                .append("where").append(" ").append(businessConditions);
        String sql = sqlbuffer.toString();
        logger.info("mysql 数据源筛选sql：{}",sql);
        return sql;
    }

    private static String getSelectFields(MysqlRunnerParams params) {
        List<FieldInfoVO> fieldInfoVOs = params.getFieldInfoVOs();
        List<String> fields = new ArrayList<>();
        for (FieldInfoVO fieldInfoVO:fieldInfoVOs) {
            String fieldName = fieldInfoVO.getFieldName();
            fields.add(fieldName);
        }
        String[] field = fields.toArray(new String[fields.size()]);
        String fieldsStr = ArrayUtil.join(field, ",");
        return fieldsStr;
    }

    /**
     * 获得筛选条件
     * @param startConfigObj
     * @return
     */
    private static   String getBusinessConditions(StartConfig startConfigObj) {
        StartConfig.TimeSelectCondition timeSelectCondition = startConfigObj.getTimeSelectCondition();
        String timeField = timeSelectCondition.getTimeField();  //时间过滤字段
        //构造时间筛选条件执行条件
        TimeSelectVO timeSelectVO = FilterOperatorUtil.getTimeSelectVO(timeSelectCondition);
        String timeCondition = timeField+" between '"+timeSelectVO.getStartTime()+"' and '"+timeSelectVO.getEndTime()+"'";

        //构造业务筛选条件
        List<String> conditions = new ArrayList<>();
        List<StartConfig.BusinessSelectField> businessSelectFields = startConfigObj.getBusinessSelectFields();
        for (StartConfig.BusinessSelectField businessSelectField:businessSelectFields) {
            String selectField = businessSelectField.getSelectField();  //筛选对象
            String relationship = businessSelectField.getRelationship();
            String threshold = businessSelectField.getThreshhold();
            String selectFieldType = businessSelectField.getSelectFieldType();
            String businessSelectCondition = getBusinessCondition(selectField, relationship, threshold, selectFieldType);
            conditions.add(businessSelectCondition);
        }
        conditions.add(timeCondition);
        String[] conditionsArray = conditions.toArray(new String[conditions.size()]);
        String selectCondition = ArrayUtil.join(conditionsArray, " and ");
        return selectCondition;
    }

    private static String getBusinessCondition(String selectField, String relationship, String threshold, String selectFieldType) {
        String businessSelectCondition = null;
        if(relationship.equals("isNull")){
             businessSelectCondition = selectField +" IS NULL";
        }else if(relationship.equals("isNotNull")){
             businessSelectCondition = selectField +" IS NOT NULL";
        }else {
            String thresholdValue = getThreshold(threshold, selectFieldType, relationship);
            businessSelectCondition = selectField +" "+ relationship +" "+ thresholdValue;
        }
        return businessSelectCondition;
    }

    private static  String getThreshold(String threshold,String selectFieldType,String relationship) {
        String[] split = threshold.split(",");
        if (split.length > 0) {
            String thresholdStr = "";
            for (String str:split) {
                if (selectFieldType.equals("varchar")) {
                    thresholdStr += "'"+str+"',";
                } else {
                    thresholdStr += str+",";
                }
            }
            thresholdStr = thresholdStr.substring(0, thresholdStr.length() - 1);
            if(relationship.equals("in") || relationship.equals("not in")){
                thresholdStr = "("+thresholdStr+")";
            }else if(relationship.equals("between") || relationship.equals("not between")){
                thresholdStr = thresholdStr.replace(",", " and ");
            }else if(relationship.equals("like") || relationship.equals("not like")){
                   thresholdStr = thresholdStr.replace("'","");
                   thresholdStr = "'%" + thresholdStr + "%'";
            }
            return thresholdStr;
        }else{
            throw new RuntimeException(threshold+"阈值格式错误");
        }
    }


}
