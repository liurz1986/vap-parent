package com.vrv.rule.source;

import com.google.gson.Gson;
import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.EventColumn;
import com.vrv.rule.model.EventTable;
import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.model.filter.*;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.udf.*;
import com.vrv.rule.source.datasourceconnector.DataStreamSourceRunner;
import com.vrv.rule.source.datasourceconnector.es.EsDataSourceInputImpl;
import com.vrv.rule.source.datasourceconnector.kafka.KafkaDataSourceInputImpl;
import com.vrv.rule.source.datasourceconnector.kafka.KafkaDataStreamSourceRunner;
import com.vrv.rule.source.datasourceconnector.mysql.MysqlDataSourceInputImpl;
import com.vrv.rule.source.datasourceconnector.mysql.MysqlDataStreamSourceRunner;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.source.datasourceparam.impl.KafkaRunnerParams;
import com.vrv.rule.source.datasourceparam.impl.MysqlRunnerParams;
import com.vrv.rule.util.*;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.MapOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获得数据源
 * @author wd-pc
 *
 */
public class GetDataSourceStream {
	
	private static Logger logger = LoggerFactory.getLogger(GetDataSourceStream.class);

	private static final Gson gson = DateUtil.parseGsonTime();


	/**
	 * 判断获得对应的data
	 * @param dataSourceInputParam
	 * @return
	 */
	private static List<String> getSources(DataSourceInputParam dataSourceInputParam){
		List<String> sources = dataSourceInputParam.getSources();
		if(sources!=null && sources.size()>0){
			return sources;
		}else {
			Exchanges exchanges = dataSourceInputParam.getExchanges();
			sources = exchanges.getSources();
			return sources;
		}
	}

	/**
	 * 数据源入口
	 * @param env
	 * @param dataSourceInputParam
	 * @return
	 */
	public static List<DataStreamSourceVO> getDataStreamSource(StreamExecutionEnvironment env, DataSourceInputParam dataSourceInputParam) {
		List<DataStreamSourceVO> dataStreamSourceVOs = new ArrayList<>();
		FilterConfigObject filterConfigObject = dataSourceInputParam.getFilterConfigObject();
		List<String> sources = getSources(dataSourceInputParam);
		Tables[][] tables = filterConfigObject.getTables();
		for (String source : sources) {
			for (int i = 0; i < tables.length; i++) {
				for (int j = 0; j < tables[i].length; j++){
					Tables table = tables[i][j];
					if(table!=null){  //TODO table可能为null
						String id = table.getId();
						String type = table.getType();
						List<Attach> attachs = table.getAttachs();
						String dataType = table.getDataType();  //dataType:1:mysql; dataType:2:es
						if(id.equals(source)){  //表示找到对应的数据源
							String tag = dataSourceInputParam.getTag();
							String roomType = dataSourceInputParam.getRoomType();
							if(type.equals("baseLine") || type.equals("eventTable")){
								if (StringUtils.isEmpty(tag) || tag.equals("online")) {
									//TODO 原先老的规则，默认是采用kafka的方式
									String groupId = dataSourceInputParam.getGroupId();
									KafkaDataSourceInputImpl kafkaDataSourceInput = new KafkaDataSourceInputImpl();
									DataStreamInputVO dataStreamInputVO = DataStreamInputVO.builder().sourceId(id).attachs(attachs)
											.env(env).tables(table).roomType(roomType).groupId(groupId).build();
									DataStreamSourceVO dataStreamSourceVO = kafkaDataSourceInput.getDataStreamSourceVO(dataStreamInputVO);
									dataStreamSourceVOs.add(dataStreamSourceVO);
								}else{
									//TODO 批量静态分析算法
									DataStreamSourceVO dataStreamSourceVO = new DataStreamSourceVO();
									String startConfig = dataSourceInputParam.getStartConfig();
									DataStreamInputVO dataStreamInputVO = DataStreamInputVO.builder().roomType(roomType).sourceId(id).attachs(attachs)
											.env(env).tables(table).startConfig(startConfig).build();
									if (dataType.equals("2")){   //mysql
										MysqlDataSourceInputImpl mysqlDataSourceInput = new MysqlDataSourceInputImpl();
										dataStreamSourceVO = mysqlDataSourceInput.getDataStreamSourceVO(dataStreamInputVO);
									}else if(dataType.equals("1")){   //es
										EsDataSourceInputImpl esDataSourceInput = new EsDataSourceInputImpl();
										dataStreamSourceVO = esDataSourceInput.getDataStreamSourceVO(dataStreamInputVO);
									}
									dataStreamSourceVOs.add(dataStreamSourceVO);
								}
							}else{
								//TODO 过滤器的引用(名称要改--暂时不用)
							}

						}
					}
				}
			}
		}
		return dataStreamSourceVOs;
	}











	/**
	 * 根据roomType数据流
	 * @param env
	 * @param roomType
	 * @param topicName
	 * @param fieldInfoVOs
	 * @return
	 */
	private static DataStream<Row> getDataStreamByRoomType(StreamExecutionEnvironment env, String roomType,
			String topicName, List<FieldInfoVO> fieldInfoVOs,String groupId) {

		DataStreamSourceRunner<KafkaRunnerParams> kafkaDataStreamSourceRunner = new KafkaDataStreamSourceRunner();
		KafkaRunnerParams kafkaRunnerParams = KafkaRunnerParams.builder().topicName(topicName).groupId(groupId).build();
		kafkaRunnerParams.setFieldInfoVOs(fieldInfoVOs);
		DataStream<Row> dataStreamSource = kafkaDataStreamSourceRunner.getDataStreamSource(env, kafkaRunnerParams);
		//DataStream<Row> dataStreamSource = KafkaDataStreamSource.getDataStreamSource(env, topicName, fieldInfoVOs,groupId);
		TypeInformation<Row> outTypeInformation = TypeInformationClass.getTypeInformationTypes(fieldInfoVOs);
		switch (roomType) {
		case RoomInfoConstant.ID_ROOM_TYPE:
			MapOrder mapTypeData = getIdRoomTypeData(fieldInfoVOs);
			dataStreamSource = mapIdRoomInfo(dataStreamSource, mapTypeData, outTypeInformation);
			break;
		case RoomInfoConstant.TIME_ROOM_TYPE:
			MapOrder timeRoomTypeData = getTimeRoomTypeData(fieldInfoVOs);
			dataStreamSource = mapTimeRoomInfo(dataStreamSource, timeRoomTypeData, outTypeInformation);
			break;
		default:
			throw new RuntimeException("roomType值为："+roomType+"不符合对应要求，请检查！");
		}
		return dataStreamSource;
	}





	/**
	 * 映射idroom相关的信息
	 * @param dataStreamSource
	 * @param mapTypeData
	 * @param outTypeInformation
	 * @return
	 */
	private static DataStream<Row> mapIdRoomInfo(DataStream<Row> dataStreamSource, MapOrder mapTypeData,
			TypeInformation<Row> outTypeInformation) {
		dataStreamSource=dataStreamSource.map(new MapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Row map(Row row) throws Exception {
				int arity = row.getArity();
				Integer order = mapTypeData.getOrder();
				String tableName = mapTypeData.getTableName();
				Object field = row.getField(order);
				if(field instanceof String){
					Map<String,String[]> map =new HashMap<>();
					String fieldValue = (String)field;
					String[] strArr = new String[] {fieldValue};
					map.put(tableName, strArr);
					row.setField(arity-1, map);
				}
				return row;
				//else{
				//	throw new RuntimeException("该数据类型不是String类型报错，请检查！,"+"order顺序:"+order+"field字段:"+field+"row:"+row);
				//}
			}
		}).returns(outTypeInformation);
		return dataStreamSource;
	}
	
	
	
	/**
	 * 构造timeRoom相关信息
	 * @param dataStreamSource
	 * @param outTypeInformation
	 * @return
	 */
	private static DataStream<Row> mapTimeRoomInfo(DataStream<Row> dataStreamSource,MapOrder mapTypeData,TypeInformation<Row> outTypeInformation) {
		dataStreamSource=dataStreamSource.map(new MapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Row map(Row row) throws Exception {
				int arity = row.getArity();
				Map<String,Map<String,String>> map =new HashMap<>();
				Integer order = mapTypeData.getOrder();
				String tableName = mapTypeData.getTableName();
				Object field = row.getField(order);
				if(field instanceof String){
					Map<String,String> timeMap = new HashMap<>();
					String eventTIme = field.toString();
					timeMap.put(RoomInfoConstant.MIN_TIME, eventTIme);
					timeMap.put(RoomInfoConstant.MAX_TIME, eventTIme);
					map.put(tableName, timeMap);
				}
//				else{
//					throw new RuntimeException("该数据类型不是String类型报错，请检查！,"+"order顺序:"+order+"field字段:"+field+"row:"+row);
//				}
				row.setField(arity-1, map);
				return row;
			}
		}).returns(outTypeInformation);
		return dataStreamSource;
	}
	



	
	public static DataStream<Row> executeMapAndFilterFunctionBysql(StreamExecutionEnvironment env, DataStream<Row> dataStream, 
			List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> outputFieldInfos, String sql,
			String tableName,String filterDesc) {
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);

		DataStreamSourceTable dataStreamSourceTable = new DataStreamSourceTable(inputFieldInfos,dataStream);
		Table  table = sTableEnv.fromTableSource(dataStreamSourceTable);
		sTableEnv.registerTable(tableName, table);

		sTableEnv.registerFunction("ipResourceFunction", new IpResourceFunction());
		sTableEnv.registerFunction("timeResourceFunction", new TimeResourceFunction());
		sTableEnv.registerFunction("portResourceFunction", new PortResourceFunction());
		sTableEnv.registerFunction("regularExpressionFunction", new RegularExpressionFunction());
		sTableEnv.registerFunction("stringResourceFunction", new StringResourceFunction());
		sTableEnv.registerFunction("timeStampFunction",new TimeStampFunction());
		sTableEnv.registerFunction("timeStampConvertDateFunction",new TimeStampConvertDateFunction());
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		TypeInformation<Row> outTypeInformation = TypeInformationClass.getTypeInformationTypes(outputFieldInfos);
		DataStream<Row> rowDataStream = retractStream.map(new MapFunction<Tuple2<Boolean,Row>, Row>(){
			private static final long serialVersionUID = 1L;
			@Override
			public Row map(Tuple2<Boolean, Row> value) throws Exception {
				Row row = value.f1;
				return row;
			}
		}).name(filterDesc).returns(outTypeInformation);
		return rowDataStream;
	}

	
 
	
    /**
     * 获得IdRoom对应的对象
     * @param outputFieldInfos
     * @return
     */
	private static MapOrder getIdRoomTypeData(List<FieldInfoVO> outputFieldInfos){
		MapOrder mapOrder = new MapOrder();
		for (FieldInfoVO fieldInfoVO : outputFieldInfos) {
			String fieldName = fieldInfoVO.getFieldName();
			if(fieldName.equals("guid")){ //TODO 规定加guid
				Integer order = fieldInfoVO.getOrder();
				String tableName = fieldInfoVO.getTableName();
				mapOrder.setOrder(order);
				mapOrder.setTableName(tableName);
			}
		}
		return mapOrder;
	}
	
	/**
	 * 获得时间MapOrder
	 * @param outputFieldInfos
	 * @return
	 */
	private static MapOrder getTimeRoomTypeData(List<FieldInfoVO> outputFieldInfos){
		MapOrder mapOrder = new MapOrder();
		FieldInfoVO eventTimeFieldInfoVO = FieldInfoUtil.getEventTimeFieldInfoVO(outputFieldInfos);
		if(eventTimeFieldInfoVO==null){
			throw new RuntimeException("原始日志没有配置事件时间字段，请检查！");
		}
		Integer order = eventTimeFieldInfoVO.getOrder();
		String tableName = eventTimeFieldInfoVO.getTableName();
		mapOrder.setOrder(order);
		mapOrder.setTableName(tableName);
		return mapOrder;
	}
	
	
	
}
