package com.vrv.rule.ruleInfo.exchangeType.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.exchangeType.agg.impl.AggregationDataStreamByKey;
import com.vrv.rule.ruleInfo.exchangeType.agg.impl.AggregationDataStreamByKeyAndWindow;
import com.vrv.rule.ruleInfo.exchangeType.agg.impl.AggregationDataStreamByWindow;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.AggregateOperator;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 聚合处理器实现
 * @author wd-pc
 *
 */
public class AggregationExchangeTypeImpl implements ExchangeType {

	@Override
	public DataStreamSourceVO exchangeType(ExchangeVO exchangeVO) {
		List<DataStreamSourceVO> dataStreamSourceVOs = exchangeVO.getDataStreamSourceVOs();
		Exchanges exchange = exchangeVO.getExchanges();
		StreamExecutionEnvironment env = exchangeVO.getEnv();
		FilterConfigObject filterConfigObject = exchangeVO.getFilterConfigObject();
		String roomType = exchangeVO.getRoomType();
		String groupId = exchangeVO.getGroupId();

		if(dataStreamSourceVOs.size()==0){ //说明为初始化数据从kafka（或者是过滤器引用）当中读取
			DataSourceInputParam dataSourceInputParam = FilterOperatorUtil.getDataSourceInputParam(exchangeVO);
			dataStreamSourceVOs = 	GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
		}
		if(dataStreamSourceVOs.size()>1) { //说明有多个输入数据源
			throw new RuntimeException("聚合处理器不存在多个输入数据源，请检查！");
		}

		Integer startIndex=groupId.lastIndexOf("-");
		//分析器code
		String filterCode=groupId.substring(startIndex+1);

		// 获取规则
		FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(filterCode);
		
		DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
		DataStream<Row> dataStreamSource = dataStreamSourceVO.getDataStreamSource();
		List<FieldInfoVO> inputFieldInfoVOs = dataStreamSourceVO.getFieldInfoVOs();
		List<FieldInfoVO> outputFieldInfoVOs = ExchangeUtil.getTargetFieldInfos(filterConfigObject, exchange,roomType);
		
		List<String> sources = exchange.getSources();
		List<Attach> attatchs = ExchangeUtil.getAggAttatchInfos(filterConfigObject, sources);
		DataStreamSourceVO dataStreamNewSourceVO = new DataStreamSourceVO();
		DataStream<Row> dataStream = null;
		String attachType = getAttachType(attatchs);
		if(attachType!=null){
			WindowConfig windowConfigs = getWindowAttachOptions(attatchs);
			switch (attachType) {
			case "all":
				String[] keys = getKeyAttachOptions(attatchs);
				AggregationDataStreamByKeyAndWindow aggregationDataStreamByKeyAndWindow = new AggregationDataStreamByKeyAndWindow();
				dataStream=aggregationDataStreamByKeyAndWindow.aggByDataStream(dataStreamSource, inputFieldInfoVOs, outputFieldInfoVOs, windowConfigs,roomType,keys);
				break;
       // TODO 取消单个key的这种情况				
			case "key":
				String[] attachOptions = getKeyAttachOptions(attatchs);
				AggregationDataStreamByKey  aggregationDataStreamByKey = new AggregationDataStreamByKey();
				dataStream = aggregationDataStreamByKey.aggByDataStream(dataStreamSource, inputFieldInfoVOs, outputFieldInfoVOs,null,roomType,attachOptions);
				break;
			case "window":
				String key = null;
				AggregationDataStreamByWindow aggregationDataStreamByWindow = new AggregationDataStreamByWindow();
				dataStream = aggregationDataStreamByWindow.aggByDataStream(dataStreamSource, inputFieldInfoVOs, outputFieldInfoVOs, windowConfigs,roomType ,key);
				break;
			default:
				break;
			}
		}else {
			throw new RuntimeException("attachType为null，请检查！");
		}
		dataStream = dealAggTypeInformation(outputFieldInfoVOs, dataStream);
		dataStream = mapDataStream(env, filterConfigObject, exchange, inputFieldInfoVOs, outputFieldInfoVOs,dataStream,roomType,filterOperator.getDesc());
		List<Attach> dataStreamAttach = ExchangeUtil.getDataStreamAttach(filterConfigObject, exchange.getTarget());
		dataStreamNewSourceVO.setDataStreamSource(dataStream);
		dataStreamNewSourceVO.setFieldInfoVOs(outputFieldInfoVOs);
		dataStreamNewSourceVO.setSourceId(exchange.getTarget());
		dataStreamNewSourceVO.setAttachs(dataStreamAttach);
		return dataStreamNewSourceVO;
	}


	/**
	 * 数据映射处理
	 * @param env
	 * @param filterConfigObject
	 * @param exchange
	 * @param inputFieldInfoVOs
	 * @param outputFieldInfoVOs
	 * @param dataStream
	 * @return
	 */
	private DataStream<Row> mapDataStream(StreamExecutionEnvironment env, FilterConfigObject filterConfigObject,
			Exchanges exchange, List<FieldInfoVO> inputFieldInfoVOs, List<FieldInfoVO> outputFieldInfoVOs,
			DataStream<Row> dataStream,String roomType,String filterDesc) {
		String inputTableName = FieldInfoUtil.getInputTableName(filterConfigObject, exchange);
		String sql = ExchangeUtil.getDataStreamAnalysisSql(filterConfigObject, exchange,roomType);
		dataStream = GetDataSourceStream.executeMapAndFilterFunctionBysql(env, dataStream, outputFieldInfoVOs, outputFieldInfoVOs, sql, inputTableName,filterDesc);
		return dataStream;
	}


	/**
	 * 处理聚合以后的数据(平均数和distincet)
	 * @param outputFieldInfoVOs
	 * @param dataStream
	 * @return
	 */
	private DataStream<Row> dealAggTypeInformation(List<FieldInfoVO> outputFieldInfoVOs, DataStream<Row> dataStream) {
		TypeInformation<Row> outTypeInformationTypes = TypeInformationClass.getTypeInformationTypes(outputFieldInfoVOs);
		dataStream=dataStream.map(new MapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Row map(Row row) throws Exception {
				for (FieldInfoVO fieldInfoVO : outputFieldInfoVOs) {
					AggregateOperator expression = fieldInfoVO.getExpression();
					if(expression!=null){
						String operator = expression.getOperator();
						Integer order = fieldInfoVO.getOrder();
						Object result = row.getField(order);
						switch (operator){
							case "avg":
								if(result instanceof Map<?,?>) {
									Map<String,Object> map = (Map<String,Object>)result;
									Object avgObj = map.get("avg");
									row.setField(order, avgObj);
								}
								break;
							case "distinctCount":
								if(result instanceof Map<?,?>){
									Map<String,Object> map = (Map<String,Object>)result;
									Object countObj = map.get("count");
									row.setField(order, countObj);
								}
								break;
							default:
								break;
						}
					}
				}
				return row;
			}
		}).returns(outTypeInformationTypes);
		return dataStream;
	}

	
	/**
	 * 获得window对应的属性
	 * @param attatchs
	 * @return
	 */
	private WindowConfig getWindowAttachOptions(List<Attach> attatchs) {
		Gson gson = new Gson();
		WindowConfig timeUnit = null;
		if(attatchs!=null){
			for (Attach attach : attatchs) {
				if(attach.getType().equals("window")){
					String options = attach.getOptions();
					timeUnit = gson.fromJson(options, WindowConfig.class);
					return timeUnit;
				}
			}
			return timeUnit;
		}else{
			throw new RuntimeException("attatchs为null，请检查！");
		}
	}
	
	/**
	 * 获得key对应的属性
	 * @param attatchs
	 * @return
	 */
	private String[] getKeyAttachOptions(List<Attach> attatchs) {
		if(attatchs!=null){
			for (Attach attach : attatchs) {
				if(attach.getType().equals("key")){
					String options = attach.getOptions();
					String[] optionArr = options.split(",");
					return optionArr;
				}
			}
			throw new RuntimeException("attatchs不包含key类型，请检查！");
		}else{
			throw new RuntimeException("attatchs为null，请检查！");
		}
	}

	/**
	 * 获得附件的类型
	 * @param attatchs
	 * @return
	 */
	private String getAttachType(List<Attach> attatchs) {
		List<String> keyAttrs = new ArrayList<>();
		if(attatchs!=null){
			for (Attach attach : attatchs) {
				keyAttrs.add(attach.getType());
			}			
		}
		if(keyAttrs.contains("key")&&keyAttrs.contains("window")){
			return "all";
		}
		
		if(keyAttrs.contains("key")){
			return "key";
		}
		
		if(keyAttrs.contains("window")){
			return "window";
		}
		return null;
	}


	
	
	

}
