package com.vrv.rule.ruleInfo.exchangeType;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.vrv.rule.model.DimensionCompletionConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vrv.rule.model.DimensionConfig;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Column;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.ruleInfo.exchangeType.agg.AggOperatorUtil;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.util.RoomInfoConstant;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.LogicOperator;
import com.vrv.rule.vo.MapOrder;

/**
 * 
 * @author wd-pc
 *
 */
public class ExchangeUtil {

	
	private static Logger logger = LoggerFactory.getLogger(ExchangeUtil.class);
	
	
	/**
	 * 获得输出数据类型（过滤和映射和joint类型输出数据）
	 * @param filterConfigObject
	 * @param exchanges
	 * @return
	 */
	public static List<FieldInfoVO> getTargetFieldInfos(FilterConfigObject filterConfigObject, Exchanges exchanges,String roomType) {
		String targetId = exchanges.getTarget();
		if(StringUtils.isNotEmpty(targetId)) { 
			Tables[][] tables = filterConfigObject.getTables();
			for (int i = 0; i < tables.length; i++) {
				for (int j = 0; j < tables[i].length; j++) {
					Tables table = tables[i][j];
					if(table!=null){
						String id = table.getId();
						if(id.equals(targetId)){
							List<Column> columns = table.getColumn();
							List<FieldInfoVO> outputFieldInfoVOs  = FieldInfoUtil.getTableFieldInfoVOsByColumns(table, columns, 0);
							setRoomTypeFieldInfo(roomType, columns.size(), table.getName(), outputFieldInfoVOs);
							return outputFieldInfoVOs;
						}						
					}
				}
			}
		}
		throw new RuntimeException(targetId+"在table当中没有发现对应的目标请检查！");
	}
	
	
	/**
	 * 获得聚合附件操作器
	 * @param filterConfigObject
	 * @param sources
	 * @return
	 */
	 public static List<Attach> getAggAttatchInfos(FilterConfigObject filterConfigObject, List<String> sources) {
		if(sources.size()==1){
			String source = sources.get(0);
			Tables[][] tables = filterConfigObject.getTables();
			for (int i = 0; i < tables.length; i++) {
				for (int j = 0; j < tables[i].length; j++) {
					Tables table = tables[i][j];
					String id = table.getId();
					if(source.equals(id)){
						List<Attach> attachs = table.getAttachs();
						return attachs;
					}
				}
			}
			return null;
		}else {
			throw new RuntimeException("聚合处理器只能够有一个数据源,请检查！");
		}
	}
	
	 /**
	  * 获得操作的附件
	  * @param filterConfigObject
	  * @param sources
	  * @return
	  */
	 public static Attach getAttatchInfos(FilterConfigObject filterConfigObject, List<String> sources,String typeName) {
		 if(sources.size()>1){
				Attach attachInfo = getAttachInfo(filterConfigObject, sources, typeName);
				return attachInfo;
			}else {
				throw new RuntimeException("连接处理器需要多个输入数据源,请检查！");
			}
		}


	private static Attach getAttachInfo(FilterConfigObject filterConfigObject, List<String> sources, String typeName) {
		String source = sources.get(0);
		Tables[][] tables = filterConfigObject.getTables();
		for (int i = 0; i < tables.length; i++) {
			for (int j = 0; j < tables[i].length; j++) {
				Tables table = tables[i][j];
				String id = table.getId();
				if(source.equals(id)){
					List<Attach> attachs = table.getAttachs();
					if(attachs!=null){
						for (Attach attach : attachs) {
							String type = attach.getType();
							if(type.equals(typeName)){
								return attach;
							}
						}
						
					}else {
						return null;
					}
				}
			}
		}
		return null;
	}
	 
	 
	 
	 /**
	  * 获得操作的附件
	  * @param filterConfigObject
	  * @param sources
	  * @return
	  */
	 public static Attach getDimensionAttatchInfos(FilterConfigObject filterConfigObject, List<String> sources,String typeName) {
		 if(sources.size()==1){
			    Attach attachInfo = getAttachInfo(filterConfigObject, sources, typeName);
				return attachInfo;
			}else {
				throw new RuntimeException("维表连接处理器只能有一个输入数据源,请检查！");
			}
		}
	 
	 
	 
	 /**
		 * 获得对应的事件设置
		 * @param timeValue
		 * @param timeUnit
		 * @return
		 */
		public static Time getTimeUnit(Long timeValue,String timeUnit){
			Time time = null;
			switch (timeUnit) {
			case "day":
				time = Time.days(timeValue);
				break;
			case "hour":
				time = Time.hours(timeValue);
				break;
			case "minutes":
				time = Time.minutes(timeValue);
				break;
			case "seconds":
				time = Time.seconds(timeValue);
				break;
			default:
				break;
			}
			return time;
		}
		
		
		/**
		 * 给数据流加一个5分钟
		 * @param dataStreamSource
		 * @param inputFieldInfos
		 * @return
		 */
		public static DataStream<Row> dataStreamAssignTimestampAndWatermarksByBound(DataStream<Row> dataStreamSource,
				List<FieldInfoVO> inputFieldInfos){
			dataStreamSource = dataStreamSource.assignTimestampsAndWatermarks(new BoundOutOfOrderNessGenerator(inputFieldInfos));
		    return dataStreamSource;
		}
		
		
		/**
		 * 给数据流加水印以增序模式
		 * @param dataStreamSource
		 * @param inputFieldInfos
		 */
		public static DataStream<Row> dataStreamAssignTimestampAndWatermarksByAscend(DataStream<Row> dataStreamSource,
				List<FieldInfoVO> inputFieldInfos) {
			 dataStreamSource=dataStreamSource.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Row>() { //这里暂时使用递增的水平形式
				private static final long serialVersionUID = 1L;
				@Override
				public long extractAscendingTimestamp(Row element) {
					FieldInfoVO fieldInfoVO = FieldInfoUtil.getEventTimeFieldInfoVO(inputFieldInfos);
					if(fieldInfoVO!=null){
						Integer order = fieldInfoVO.getOrder();
						Object eventTimeOrder = element.getField(order);
						if(eventTimeOrder instanceof String){
							String eventTime = (String)eventTimeOrder; //yyyy-mm-dd hh:mm:ss
							Long timestamp = DateUtil.getTimestamp(eventTime, DateUtil.DEFAULT_DATE_PATTERN);
							return timestamp;													
						}else {
							throw new RuntimeException(eventTimeOrder+"不是字符串类型，请检查！");
						}
					}else {
						throw new RuntimeException("该表没有eventtime字段，请检查！");
					}
				}
			});
			 return dataStreamSource;
		}
		
	/**
	 * 获得聚合的row
	 * @param inputFieldInfos
	 * @param outputFieldInfos
	 * @param outputRow
	 * @param accumulatorRow
	 * @param inputRow
	 * @return
	 */
	public static Row getAccRow(List<FieldInfoVO> inputFieldInfos, List<FieldInfoVO> outputFieldInfos,
				Row accumulatorRow, Row inputRow,String roomType) {
			if(AggOperatorUtil.judgeIsInitRow(accumulatorRow)){
				Row outputRows = new Row(outputFieldInfos.size());
				AggOperatorUtil.setSameFieldRow(inputFieldInfos, outputFieldInfos, inputRow, outputRows);
				AggOperatorUtil.getAggregateOperator(outputRows, outputFieldInfos,inputRow,inputFieldInfos);
				return outputRows;
			}else{
				AggOperatorUtil.setSameFieldRowWithoutGuid(inputFieldInfos, outputFieldInfos, inputRow, accumulatorRow);
				AggOperatorUtil.getAggregateOperator(accumulatorRow, outputFieldInfos,inputRow,inputFieldInfos);
				accumulatorRow = ExchangeUtil.setRoomInfoByType(inputRow, accumulatorRow, roomType, outputFieldInfos, inputFieldInfos);
				return accumulatorRow;
			}
		}

    /**
     * 设置对应的roomId 
     * @param accumulatorRow
     * @param inputIdRoomOrder
     * @param outputIdRoomOrder
     * @param inputRoomId
     * @param outRoomId
     */
	public static void setRoomId(Row accumulatorRow, MapOrder outputIdRoomOrder,
			Object inputRoomId, Object accRoomId) {
		if(inputRoomId instanceof Map<?, ?> && accRoomId instanceof Map<?, ?>){
			Map<String, String[]> inputMap = (Map<String, String[]>)inputRoomId;
			Map<String, String[]> accMap = (Map<String, String[]>)accRoomId;
			for(Map.Entry<String, String[]> entry : inputMap.entrySet()){
				String key = entry.getKey();
				String[] inputValue = entry.getValue();
				if(accMap.containsKey(key)){
					String[] accStr = accMap.get(key);
					Set<String> inputs = new HashSet<>(Arrays.asList(inputValue));
					Set<String> accputs = new HashSet<>(Arrays.asList(accStr));
					accputs.addAll(inputs);
					String[] outputArr = accputs.toArray(new String[accputs.size()]);
					accMap.put(key, outputArr);
				
				}else{
					accMap.putAll(inputMap);
				}
			}
			accumulatorRow.setField(outputIdRoomOrder.getOrder(), accMap);
		}
	}
	
	/**
	 * 获得roomType数据
	 * @param fieldInfos
	 * @param fType(mapArray,mapMap)
	 * @return
	 */
	public static MapOrder getRoomOrder(List<FieldInfoVO> fieldInfos,String roomType){
		MapOrder mapOrder = new MapOrder();
		for (FieldInfoVO fieldInfoVO : fieldInfos) {
			String fieldName = fieldInfoVO.getFieldName();
			if(fieldName.equals(roomType)){
				Integer order = fieldInfoVO.getOrder();
				String tableName = fieldInfoVO.getTableName();
				mapOrder.setOrder(order);
				mapOrder.setTableName(tableName);
				return mapOrder;
			}
		}
		throw new RuntimeException("该数据类型没有"+roomType+"类型，请检查！");
	}
	
	/**
	 * 返回table上面对应的附件
	 * @param filterConfigObject
	 * @param targetId
	 * @return
	 */
	public static List<Attach> getDataStreamAttach(FilterConfigObject filterConfigObject,String targetId){
		List<Attach>  list = new ArrayList<>();
		Tables[][] tables = filterConfigObject.getTables();
		for (int i = 0; i < tables.length; i++) {
			for (int j = 0; j < tables[i].length; j++) {
				Tables table = tables[i][j];
				if(table!=null) {
					String id = table.getId();
					if(id.equals(targetId)) {
						list = table.getAttachs();
						return list;
					}
					
				}
			}
		}
		return list;
	}
	
	
	
	/**
	 * 获得维表附件关联得操作信息
	 * @param attatch
	 * @return
	 */
	public static DimensionConfig getDimensionAttachOptions(Attach attatch) {
		Gson gson = new Gson();
		if(attatch!=null){
			if(attatch.getType().equals("dimension")){
				String options = attatch.getOptions();
				DimensionConfig dimensionConfig = gson.fromJson(options, DimensionConfig.class);
				return dimensionConfig;
			}
			throw new RuntimeException("维表连接处理器附必须是dimension类型，请检查！");
		}
		throw new RuntimeException("维表连接处理器附必须含有attach附件，请检查！");
	}

	/**
	 * 获得补全维表附件关联得操作信息
	 * @param attatch
	 * @return
	 */
	public static DimensionCompletionConfig getDimensionCollectionAttachOptions(Attach attatch) {
		Gson gson = new Gson();
		if(attatch!=null){
			if(attatch.getType().equals("dimensionCollection")){
				String options = attatch.getOptions();
				DimensionCompletionConfig dimensionCompletionConfig = gson.fromJson(options, DimensionCompletionConfig.class);
				return dimensionCompletionConfig;
			}
			throw new RuntimeException("维表连接处理器附必须是dimensionCompletionConfig类型，请检查！");
		}
		throw new RuntimeException("维表连接处理器附必须含有attach附件，请检查！");
	}

	
	
	/**
	 *获得对应的sql
	 */
	public static String getDataStreamAnalysisSql(FilterConfigObject filterConfigObject,Exchanges exchanges,String roomType){
		String tableName = FieldInfoUtil.getInputTableName(filterConfigObject, exchanges);
		String fieldInfosStr = FieldInfoUtil.getFieldInfosStr(filterConfigObject, exchanges,roomType);
		String filterConditions = getFilterConditions(exchanges);
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotEmpty(filterConditions)){
			sb.append("select").append(" ").append(fieldInfosStr).append(" ").append("from").append(" ").append(tableName).append(" ").append("where").append(" ").append(filterConditions);			
		}else{
			sb.append("select").append(" ").append(fieldInfosStr).append(" ").append("from").append(" ").append(tableName).append(" ").append("where").append(" ").append("1=1");			
		}
		String sql = sb.toString();
		logger.info("映射处理器sql:"+sql);
		return sql;
	}

	
	private static String getFilterConditions(Exchanges exchanges) {
		String options = exchanges.getOptions();
		if(StringUtils.isNotEmpty(options)){
			Gson gson = new Gson();
			LogicOperator logicOperator = gson.fromJson(options, LogicOperator.class);
			String filterCondition = logicOperator.getFilterCondition();
			return filterCondition;
		}else {
			return "";
		}
	}

	
	/**
	 * 设置对应RoomTypeInfo对应的值
	 * @param roomType
	 * @param columnList
	 * @param name
	 * @param fieldInfoVOs
	 */
	public static void setRoomTypeFieldInfo(String roomType, Integer columnListSize , String tableName,List<FieldInfoVO> fieldInfoVOs) {
		FieldInfoVO fieldInfoVO = new FieldInfoVO();
		switch (roomType){
		case RoomInfoConstant.ID_ROOM_TYPE:
			   fieldInfoVO = FieldInfoUtil.getRoomFieldInfo(tableName, UUID.randomUUID().toString(),columnListSize,roomType,RoomInfoConstant.MAP_ARRAY);
			   break;
          case RoomInfoConstant.TIME_ROOM_TYPE:
        	   fieldInfoVO = FieldInfoUtil.getRoomFieldInfo(tableName, UUID.randomUUID().toString(),columnListSize,roomType,RoomInfoConstant.MAP_MAP);
			   break;
		default:
			throw new RuntimeException("roomType值为："+roomType+"不符合对应要求，请检查！");
		}
		fieldInfoVOs.add(fieldInfoVO);
	}
	
	/**
	 * 设置roomId属性
	 * @param inputRow
	 * @param accumulator
	 * @param outputFieldInfos
	 * @param inputFieldInfos
	 * @return
	 */
	public static Row setRoomTypeById(Row inputRow, Row accumulator,List<FieldInfoVO> outputFieldInfos,List<FieldInfoVO> inputFieldInfos) {
		MapOrder inputIdRoomOrder = getRoomOrder(inputFieldInfos,RoomInfoConstant.ID_ROOM_TYPE);
		MapOrder outputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.ID_ROOM_TYPE);
		Object inputRoomId = inputRow.getField(inputIdRoomOrder.getOrder());
		Object accRoomId = accumulator.getField(outputIdRoomOrder.getOrder());
		setRoomId(accumulator, outputIdRoomOrder, inputRoomId, accRoomId);
		return accumulator;
	}
	
	/**
	 * 通过reduce设置roomId属性
	 * @param inputRow
	 * @param accumulator
	 * @param outputFieldInfos
	 * @return
	 */
	public static Row setRoomTypeByIdByReduce(Row inputRow, Row accumulator,List<FieldInfoVO> outputFieldInfos) {
		MapOrder inputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.ID_ROOM_TYPE);
		MapOrder outputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.ID_ROOM_TYPE);
		Object inputRoomId = inputRow.getField(inputIdRoomOrder.getOrder());
		Object accRoomId = accumulator.getField(outputIdRoomOrder.getOrder());
		setRoomId(accumulator, outputIdRoomOrder, inputRoomId, accRoomId);
		return accumulator;
	}
	
	
	
	
	/**
	 * 设置roomTime属性
	 * @param inputRow
	 * @param accumulator
	 * @param outputFieldInfos
	 * @param inputFieldInfos
	 * @return
	 */
	public static Row setRoomTypeByTime(Row inputRow, Row accumulator,List<FieldInfoVO> outputFieldInfos,List<FieldInfoVO> inputFieldInfos){
		MapOrder inputIdRoomOrder = getRoomOrder(inputFieldInfos,RoomInfoConstant.TIME_ROOM_TYPE);
		MapOrder outputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.TIME_ROOM_TYPE);
		Object inputRoomTime = inputRow.getField(inputIdRoomOrder.getOrder());
		Object accRoomTime = accumulator.getField(outputIdRoomOrder.getOrder());
		accumulator = setRoomTime(accumulator, outputIdRoomOrder, inputRoomTime, accRoomTime);
		return accumulator;
	}
	
	
	
	/**
	 *  通过reduce设置roomTime属性
	 * @param inputRow
	 * @param accumulator
	 * @param outputFieldInfos
	 * @return
	 */
	public static Row setRoomTypeByTimeByReduce(Row inputRow, Row accumulator,List<FieldInfoVO> outputFieldInfos){
		MapOrder inputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.TIME_ROOM_TYPE);
		MapOrder outputIdRoomOrder = getRoomOrder(outputFieldInfos,RoomInfoConstant.TIME_ROOM_TYPE);
		Object inputRoomTime = inputRow.getField(inputIdRoomOrder.getOrder());
		Object accRoomTime = accumulator.getField(outputIdRoomOrder.getOrder());
		accumulator = setRoomTime(accumulator, outputIdRoomOrder, inputRoomTime, accRoomTime);
		return accumulator;
	}
	
	


	private static Row setRoomTime(Row accumulator, MapOrder outputIdRoomOrder, Object inputRoomTime,
			Object accRoomTime) {
		if(inputRoomTime instanceof Map<?, ?> && accRoomTime instanceof Map<?, ?>){
			Map<String, Map<String,String>> inputMap = (Map<String, Map<String,String>>)inputRoomTime;
			Map<String, Map<String,String>> accMap = (Map<String, Map<String,String>>)accRoomTime;
			for(Map.Entry<String, Map<String,String>> entry : inputMap.entrySet()){
				String key = entry.getKey();
				Map<String,String> inputValue = entry.getValue();
				if(accMap.containsKey(key)){
					Map<String,String> accTime = accMap.get(key);
					try {
						Date inputMinTime = DateUtil.parseDate(inputValue.get(RoomInfoConstant.MIN_TIME), DateUtil.DEFAULT_DATE_PATTERN);
						Date inputMaxTime = DateUtil.parseDate(inputValue.get(RoomInfoConstant.MAX_TIME), DateUtil.DEFAULT_DATE_PATTERN);
						Date accMinTime = DateUtil.parseDate(accTime.get(RoomInfoConstant.MIN_TIME), DateUtil.DEFAULT_DATE_PATTERN);
						Date accMaxTime = DateUtil.parseDate(accTime.get(RoomInfoConstant.MAX_TIME), DateUtil.DEFAULT_DATE_PATTERN);
						if(inputMinTime.getTime()<accMinTime.getTime()&&inputMinTime.getTime()<accMaxTime.getTime()) {
							accTime.put(RoomInfoConstant.MIN_TIME, inputValue.get(RoomInfoConstant.MIN_TIME));
						}
						if(inputMaxTime.getTime()>accMaxTime.getTime()&&inputMaxTime.getTime()>accMinTime.getTime()){
							accTime.put(RoomInfoConstant.MAX_TIME, inputValue.get(RoomInfoConstant.MAX_TIME));
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					accMap.put(key, accTime);
				
				}else{
					accMap.putAll(inputMap);
				}
			}
			accumulator.setField(outputIdRoomOrder.getOrder(), accMap);
			return accumulator;
		}else {
			throw new RuntimeException("inputRoomTime，accRoomTime不是map类型，请检查！");
		}
	}
	
	
	/**
	 * 根据盒子类型设置RoomInfo信息
	 * @param inputRow
	 * @param accumulator
	 * @param roomType
	 * @param outputFieldInfos
	 * @param inputFieldInfos
	 * @return
	 */
	public static Row setRoomInfoByType(Row inputRow, Row accumulator,String roomType,List<FieldInfoVO> outputFieldInfos,List<FieldInfoVO> inputFieldInfos) {
		switch (roomType) {
		case RoomInfoConstant.ID_ROOM_TYPE:
			accumulator = setRoomTypeById(inputRow, accumulator, outputFieldInfos, inputFieldInfos);
			break;
		case RoomInfoConstant.TIME_ROOM_TYPE:
			accumulator = setRoomTypeByTime(inputRow, accumulator, outputFieldInfos, inputFieldInfos);
			break;
		default:
			break;
		}
		return accumulator;
	}

	
	
	/**
	 * 根据盒子类型设置RoomInfo信息（reduce特殊的方式）
	 * @param inputRow
	 * @param accumulator
	 * @param roomType
	 * @param outputFieldInfos
	 * @param inputFieldInfos
	 * @return
	 */
	public static Row setRoomInfoByTypeAndReduce(Row inputRow, Row accumulator,String roomType,List<FieldInfoVO> outputFieldInfos) {
		switch (roomType) {
		case RoomInfoConstant.ID_ROOM_TYPE:
			accumulator = setRoomTypeByIdByReduce(inputRow, accumulator, outputFieldInfos);
			break;
		case RoomInfoConstant.TIME_ROOM_TYPE:
			accumulator = setRoomTypeByTimeByReduce(inputRow, accumulator, outputFieldInfos);
			break;
		default:
			break;
		}
		return accumulator;
	}
	
	
	
	
	
	
}
