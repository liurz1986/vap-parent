package com.flink.demo.analysis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import com.flink.demo.vo.POJOTestVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.ruleInfo.udf.AccountFilterFunction;
import com.vrv.rule.source.DataStreamSourceTable;
import com.vrv.rule.source.KafkaDataStreamSource;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.vo.FieldInfoVO;

public class AlarmAnalysisFunction {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static void main(String[] args) throws Exception {
		LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
		List<FieldInfoVO> inputList = POJOTestVO.getFieldInfoList();
		List<FieldInfoVO> outputlist = POJOTestVO.getFieldInfoListAggreat();
		DataStream<Row> dataStreamSource = KafkaDataStreamSource.getDataStreamSource(localEnvironment, inputList);
		
//		DataStreamAPIModelSet dataStreamAPIModelSet = new DataStreamAPIModelSet();
//		DataStream<Row> dataStream = dataStreamAPIModelSet.foldByDataStreamByKey(dataStreamSource, "assetGuid", inputList, outputlist);
//		dataStream.print();

//		String mapsql = "select collectorIp,cou(counts/5)*5 as countss,assetGuid as switchId,"
//				+ "ifEntryList[1].ifDescr as ifEntry,runningDetail.bytesSent as byteSends from SwitchVo";
		//
//		String aggreateSql = "select accountFilterFunction(speedRate,'>',1500) as counts,count(*) as total from SwitchVo"; 
//		String maptableName = "SwitchVo";
//		DataStream<Row> dataStream = executeInvokeFilterAndMapMethod(localEnvironment,
//				dataStreamSource,inputList,outputlist,aggreateSql,maptableName,"mapDataStreamFunctionBySql");
//		dataStream.print();
////		
//		String filtersql = "select collectorIp from SwitchFilterVo";
//		String filtertableName = "SwitchFilterVo";
//		List<FieldInfoVO> lastFields = POJOTestVO.getFieldInfoListFilterFinally();
//		DataStream<Row> dataFilterStream = executeInvokeFilterAndMapMethod(localEnvironment,
//				dataStream,outputlist,lastFields,filtersql,filtertableName,"filterDataStreamFunctionBySql");
//		dataFilterStream.print();
		
		localEnvironment.execute("job Name");
	}
	
	
	
	
	
	
	

	/**
	 * 执行反射映射和过滤对应的方法
	 * @param localEnvironment
	 * @param dataStreamSource
	 * @param selectField
	 * @param sql
	 * @param tableName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	private static DataStream<Row> executeInvokeFilterAndMapMethod(LocalStreamEnvironment localEnvironment, DataStream<Row> dataStreamSource,
			List<FieldInfoVO> inputfields,List<FieldInfoVO> outfields,String sql,String tableName,String methodName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		Class<?> clazz = Class.forName("com.vrv.rule.demo.AnalysisDemo.AlarmAnalysisFunction");
		Object object = clazz.newInstance();
		Method method = clazz.getDeclaredMethod(methodName,LocalStreamEnvironment.class,DataStream.class,List.class,List.class,String.class,String.class);
		Object invoke = method.invoke(object, localEnvironment,dataStreamSource,inputfields,outfields,sql,tableName);
		DataStream<Row> dataStream=(DataStream<Row>)invoke;
		return dataStream;
	}
	/**
	 *  通过sql的方式实现映射
	 * @param localEnvironment
	 * @param dataStream
	 * @param selectField
	 * @param sql
	 * @param tableName
	 * @return
	 */
	public <T> DataStream<Row> mapDataStreamFunctionBySql(LocalStreamEnvironment localEnvironment,DataStream<Row> dataStream,List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> outputFieldInfos,String sql,String tableName){
		DataStream<Row> stream = executeMapAndFilterFunctionBysql(localEnvironment, dataStream, inputFieldInfos,outputFieldInfos, sql, tableName);
		 return stream;
	}
	
	/**
	 * 通过sql的方式实现过滤
	 */
	public  <T> DataStream<Row> filterDataStreamFunctionBySql(LocalStreamEnvironment localEnvironment,DataStream<Row> dataStream,List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> outputFieldInfos,String sql,String tableName){
		DataStream<Row> stream = executeMapAndFilterFunctionBysql(localEnvironment, dataStream, inputFieldInfos,outputFieldInfos ,sql, tableName);
		 return stream;
	}
	

	private DataStream<Row> executeMapAndFilterFunctionBysql(
			LocalStreamEnvironment localEnvironment, DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> outputFieldInfos, String sql,
			String tableName) {
        EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(localEnvironment,fsSettings);
		
		DataStreamSourceTable dataStreamSourceTable = new DataStreamSourceTable(inputFieldInfos,dataStream);
		//sTableEnv.registerTableSource(tableName, dataStreamSourceTable);
		sTableEnv.registerDataStream(tableName, dataStream);
		sTableEnv.registerFunction("accountFilterFunction", new AccountFilterFunction());
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		TypeInformation<Row> outTypeInformation = TypeInformationClass.getTypeInformationTypes(outputFieldInfos);
		DataStream<Row> rowDataStream = retractStream.map(new MapFunction<Tuple2<Boolean,Row>, Row>(){
			@Override
			public Row map(Tuple2<Boolean, Row> value) throws Exception {
				Row row = value.f1;
				return row;
			}
		}).returns(outTypeInformation);
		return rowDataStream;
	}
	
	
	
	
	
	
	
	
}
