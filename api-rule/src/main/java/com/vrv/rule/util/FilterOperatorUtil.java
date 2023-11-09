package com.vrv.rule.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vrv.rule.ruleInfo.exchangeType.impl.*;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.source.datasourceparam.StartConfig;
import com.vrv.rule.vo.DimensionKeyVO;
import com.vrv.rule.vo.ExchangeVO;
import com.vrv.rule.vo.TimeSelectVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeTypeExecutor;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.vo.FieldInfoVO;

import javax.xml.crypto.Data;

/**
 * 过滤器的工具类
 *
 * @author wd-pc
 */
public class FilterOperatorUtil {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    /**
     * 获得对应的FilterTable数据
     *
     * @param code
     * @return
     */
    public static FilterOperator getFilterOperator(String code) {
        List<String> list = new ArrayList<>();
        list.add(code);
        String sql = JdbcSqlConstant.FILTER_TABLE_SQL;
        List<FilterOperator> filterOperators = JdbcSingeConnectionUtil.getInstance().querySqlForFilterOperator(sql, list);
        if (filterOperators.size() == 1) {
            FilterOperator filterOperator = filterOperators.get(0);
            return filterOperator;
        } else {
            throw new RuntimeException("该过滤器" + code + "查询对象不是唯一，请检查！");
        }
    }


    /**
     * 执行exchange执行器
     * @param exchangeVO
     * @return
     */
    public static List<DataStreamSourceVO> executeExchangeOperator(ExchangeVO exchangeVO) {
        Exchanges exchange = exchangeVO.getExchanges();
        List<DataStreamSourceVO> dataStreamSourceVOs = exchangeVO.getDataStreamSourceVOs();
        String type = exchange.getType();
        switch (type) {
            case "filter": //过滤
            case "map": //映射
                ExchangeType mapExchangeType = new MapExchangeTypeImpl();
                ExchangeTypeExecutor mapExchangeTypeExecutor = new ExchangeTypeExecutor(mapExchangeType, exchangeVO);
                dataStreamSourceVOs = mapExchangeTypeExecutor.executeExchangeType();
                break;
            case "aggregate": //聚合
                ExchangeType aggregationExchangeType = new AggregationExchangeTypeImpl();
                ExchangeTypeExecutor aggregationExecutor = new ExchangeTypeExecutor(aggregationExchangeType, exchangeVO);
                dataStreamSourceVOs = aggregationExecutor.executeExchangeType();
                break;
            case "join"://连接
                ExchangeType joinExchangeType = new JoinExchangeTypeImpl();
                ExchangeTypeExecutor joinExecutor = new ExchangeTypeExecutor(joinExchangeType,exchangeVO);
                dataStreamSourceVOs = joinExecutor.executeExchangeType();
                break;
            case "flapMap"://分解
                ExchangeType flatExchangeType = new FlatMapExchangeTypeImpl();
                ExchangeTypeExecutor flatMapExecutor = new ExchangeTypeExecutor(flatExchangeType, exchangeVO);
                dataStreamSourceVOs = flatMapExecutor.executeExchangeType();
                break;
            case "union"://拼接
                ExchangeType unionExchangeType = new UnionExchangeTypeImpl();
                ExchangeTypeExecutor unionExecutor = new ExchangeTypeExecutor(unionExchangeType, exchangeVO);
                dataStreamSourceVOs = unionExecutor.executeExchangeType();
                break;
            case "dimensionJoin"://维表连接
                ExchangeType dimensionJoinExchangeType = new DimensionJoinExchangeTypeImpl();
                ExchangeTypeExecutor dimensionJoinExecutor = new ExchangeTypeExecutor(dimensionJoinExchangeType, exchangeVO);
                dataStreamSourceVOs = dimensionJoinExecutor.executeExchangeType();
                break;
            case "dimensionFilter"://维表过滤
                ExchangeType dimensionFilterExchangeType = new DimensionFilterExchangeTypeImpl();
                ExchangeTypeExecutor dimensionFilterExecutor = new ExchangeTypeExecutor(dimensionFilterExchangeType,exchangeVO);
                dataStreamSourceVOs = dimensionFilterExecutor.executeExchangeType();
                break;
            case "dimensionCollection"://维表补全
                ExchangeType dimensionCollectionExchangeType = new DimensionCollectionExchangeTypeImpl();
                ExchangeTypeExecutor dimensionCollectionExecutor = new ExchangeTypeExecutor(dimensionCollectionExchangeType,exchangeVO);
                dataStreamSourceVOs = dimensionCollectionExecutor.executeExchangeType();
                break;

            default:
                break;
        }
        return dataStreamSourceVOs;
    }


    public static List<DataStreamSourceVO> executeFilterOperator(StreamExecutionEnvironment env, FilterOperator filterOperator, String groupId, List<DataStreamSourceVO> dataStreamSourceVOs, String ruleCode) {
        String config = filterOperator.getConfig();
        String roomType = filterOperator.getRoomType();
        String startConfig = filterOperator.getStartConfig();
        String tag = filterOperator.getTag();
        FilterConfigObject filterConfigObject = gson.fromJson(config, FilterConfigObject.class);
        Exchanges[][] exchanges = filterConfigObject.getExchanges();
        int exchangeCount = getExchangeCount(exchanges);
        if (exchangeCount != 0) {


            for (int i = 0; i < exchanges.length; i++) {
                for (int j = 0; j < exchanges[i].length; j++) {
                    Exchanges exchange = exchanges[i][j];
                    ExchangeVO exchangeVO = ExchangeVO.builder().env(env).exchanges(exchange)
                            .filterConfigObject(filterConfigObject).tag(tag)
                            .ruleCode(ruleCode).groupId(groupId)
                            .dataStreamSourceVOs(dataStreamSourceVOs)
                            .startConfig(startConfig).roomType(roomType).build();
                    List<DataStreamSourceVO> list = executeExchangeOperator(exchangeVO);
                    dataStreamSourceVOs = list;
                }
            }
        } else {
            List<String> sources = getTableSources(filterConfigObject);
            DataSourceInputParam dataSourceInputParam = DataSourceInputParam.builder().sources(sources).tag(tag)
                    .startConfig(startConfig).filterConfigObject(filterConfigObject)
                    .groupId(groupId).roomType(roomType).build();
            dataStreamSourceVOs = GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
        }
        return dataStreamSourceVOs;
    }


    /**
     * 执行过滤器获得对应的数据流
     *
     * @param env
     * @param filterOperator
     * @return
     */
    public static List<DataStreamSourceVO> executeFilterOperator(StreamExecutionEnvironment env,FilterOperator filterOperator, String groupId, String ruleCode) {
        String config = filterOperator.getConfig();
        String roomType = filterOperator.getRoomType();
        String startConfig = filterOperator.getStartConfig();
        String tag = filterOperator.getTag();
        FilterConfigObject filterConfigObject = gson.fromJson(config, FilterConfigObject.class);
        Exchanges[][] exchanges = filterConfigObject.getExchanges();
        List<DataStreamSourceVO> dataStreamSourceVOs = new ArrayList<>();
        int exchangeCount = getExchangeCount(exchanges);  //算子的个数
        if (exchangeCount != 0) {
            for (int i = 0; i < exchanges.length; i++) {
                for (int j = 0; j < exchanges[i].length; j++) {
                    Exchanges exchange = exchanges[i][j];
                    ExchangeVO exchangeVO = ExchangeVO.builder().env(env).exchanges(exchange)
                            .filterConfigObject(filterConfigObject).tag(tag)
                            .ruleCode(ruleCode).groupId(groupId)
                            .dataStreamSourceVOs(dataStreamSourceVOs)
                            .startConfig(startConfig).roomType(roomType).build();
                    List<DataStreamSourceVO> list = executeExchangeOperator(exchangeVO);
                    dataStreamSourceVOs = list;
                }
            }
        } else {
            List<String> sources = getTableSources(filterConfigObject);
            DataSourceInputParam dataSourceInputParam = DataSourceInputParam.builder().sources(sources).tag(tag)
                    .startConfig(startConfig).filterConfigObject(filterConfigObject)
                    .groupId(groupId).roomType(roomType).build();
            dataStreamSourceVOs = GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
        }
        return dataStreamSourceVOs;
    }


    /**
     * 获得exchange的count
     *
     * @param exchanges
     * @return
     */
    private static int getExchangeCount(Exchanges[][] exchanges) {
        int count = 0;
        for (int i = 0; i < exchanges.length; i++) {
            if (exchanges[i].length != 0) {
                count = exchanges[i].length;
            }
        }
        return count;
    }


    /**
     * 获得对应表的guid
     *
     * @param filterConfigObject
     * @return
     */
    private static List<String> getTableSources(FilterConfigObject filterConfigObject) {
        List<String> sources = new ArrayList<>();
        Tables[][] tables = filterConfigObject.getTables();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].length; j++) {
                Tables table = tables[i][j];
                if (table != null) {
                    String id = table.getId();
                    sources.add(id);
                }
            }
        }
        return sources;
    }


    /**
     * 数据流配置出对应的类型
     *
     * @param dataStream
     * @param outputFieldInfoVOs
     * @return
     */
    public static DataStream<Row> convertDataStreamWithTypeInformation(DataStream<Row> dataStream, List<FieldInfoVO> outputFieldInfoVOs) {
        TypeInformation<Row> outTypeInformationTypes = TypeInformationClass.getTypeInformationTypes(outputFieldInfoVOs);
        dataStream = dataStream.map(new MapFunction<Row, Row>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Row map(Row value) throws Exception {
                return value;
            }
        }).returns(outTypeInformationTypes);
        return dataStream;
    }

    /**
     * 数据流配置出对应的类型
     *
     * @param dataStream
     * @param outputFieldInfoVOs
     * @return
     */
    public static DataStream<Row> convertDataStreamWithTypeInformation(DataStream<Row> dataStream, List<FieldInfoVO> outputFieldInfoVOs, String filterDesc) {
        TypeInformation<Row> outTypeInformationTypes = TypeInformationClass.getTypeInformationTypes(outputFieldInfoVOs);
        dataStream = dataStream.map(new MapFunction<Row, Row>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Row map(Row value) throws Exception {
                return value;
            }
        }).name(filterDesc).returns(outTypeInformationTypes);
        return dataStream;
    }


    /**
     * 获得时间选择
     *
     * @param timeSelectCondition
     * @return
     */
    public static TimeSelectVO getTimeSelectVO(StartConfig.TimeSelectCondition timeSelectCondition) {
        String type = timeSelectCondition.getType(); //时间筛选类型
        String startTime = null;
        String endTime = null;
        switch (type) {
            case "hour":
                String baseHourTime = DateUtil.format(DateUtil.addHours(new Date(), -1), DateUtil.yyyy_mm_dd_hh);
                startTime = baseHourTime + ":00:00";
                endTime = baseHourTime + ":59:59";
                break;
            case "day":
                //时间范围
                String baseDayTime = DateUtil.format(DateUtil.addDay(new Date(), -1), DateUtil.yyyy_mm_dd);
                startTime = baseDayTime + " 00:00:00";
                endTime = baseDayTime + " 23:59:59";
                break;
            case "week":
                String baseWeekTime = DateUtil.format(DateUtil.addDay(new Date(), -7), DateUtil.yyyy_mm_dd);
                startTime = baseWeekTime + " 00:00:00";
                String baseWeekTimeEnd = DateUtil.format(new Date(), DateUtil.yyyy_mm_dd);
                endTime = baseWeekTimeEnd + " 23:59:59";
                break;
            case "month":
                int currentMonth = DateUtil.getCurrentMonth();
                int currentMonthDays = DateUtil.getCurrentMonthDays(currentMonth);
                String baseMonthTime = DateUtil.format(DateUtil.addDay(new Date(), -currentMonthDays), DateUtil.yyyy_mm_dd);
                startTime = baseMonthTime + " 00:00:00";
                String baseMonthTimeEnd = DateUtil.format(new Date(), DateUtil.yyyy_mm_dd);
                endTime = baseMonthTimeEnd + " 23:59:59";
                break;
            default:
                break;
        }
        TimeSelectVO timeSelectVO1 = TimeSelectVO.builder().startTime(startTime).endTime(endTime).build();
        return timeSelectVO1;
    }


    public static DataSourceInputParam getDataSourceInputParam(ExchangeVO exchangeVO) {
        DataSourceInputParam dataSourceInputParam = DataSourceInputParam.builder().tag(exchangeVO.getTag())
                .roomType(exchangeVO.getRoomType())
                .exchanges(exchangeVO.getExchanges())
                .filterConfigObject(exchangeVO.getFilterConfigObject()).startConfig(exchangeVO.getStartConfig())
                .groupId(exchangeVO.getGroupId()).build();

        return dataSourceInputParam;
    }


    /**
     * 获得维度数据的缓存的key
     * @param dimensionKeyVO
     * @return
     */
    public static String  getDimensionDataKey(DimensionKeyVO dimensionKeyVO){
        StringBuffer sb = new StringBuffer();
        collectionCondition(sb, dimensionKeyVO.getDimensionTableName());
        collectionCondition(sb, dimensionKeyVO.getDimensionFieldName());
        collectionCondition(sb, dimensionKeyVO.getFilterCondition());


        collectionCondition(sb, dimensionKeyVO.getHighFilterCondition());
        collectionCondition(sb, dimensionKeyVO.getFilterCode());
        collectionCondition(sb, dimensionKeyVO.getRuleCode());
        String result = sb.toString();
        result = result.substring(0, result.lastIndexOf("&"));
        return result;
    }

    private static void collectionCondition(StringBuffer sb, String dimensionInfo) {
        if(StringUtils.isNotEmpty(dimensionInfo)){
            sb.append(dimensionInfo).append("&");
        }
    }


}
