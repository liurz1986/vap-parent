package com.vrv.rule.ruleInfo.exchangeType.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.JoinedStreams;
import org.apache.flink.streaming.api.datastream.JoinedStreams.WithWindow;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.CountEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.exchangeType.join.KeySelectorFunction;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.util.RoomInfoConstant;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.MapOrder;

/**
 * 数据流连接处理器
 * @author wd-pc
 *
 */
public class JoinExchangeTypeImpl implements ExchangeType,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public DataStreamSourceVO exchangeType(ExchangeVO exchangeVO) {
		List<DataStreamSourceVO> dataStreamSourceVOs = exchangeVO.getDataStreamSourceVOs();
		Exchanges exchange = exchangeVO.getExchanges();
		StreamExecutionEnvironment env = exchangeVO.getEnv();
		FilterConfigObject filterConfigObject = exchangeVO.getFilterConfigObject();
		String roomType = exchangeVO.getRoomType();
		String groupId = exchangeVO.getGroupId();
		String startConfig = exchangeVO.getStartConfig();

		if(dataStreamSourceVOs.size()==0){ //说明为初始化数据从kafka（或者是过滤器引用）当中读取
			DataSourceInputParam dataSourceInputParam = DataSourceInputParam.builder().tag(exchangeVO.getTag())
					.roomType(roomType)
					.exchanges(exchange)
					.filterConfigObject(filterConfigObject).startConfig(startConfig)
					.groupId(groupId).build();
			dataStreamSourceVOs = 	GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
		}
		if(dataStreamSourceVOs.size()==1){ //数据流连接处理器必须有两个的数据流，这个地方需要需要进行判断
			List<String> sources = exchange.getSources();
			List<DataStreamSourceVO> joinExtraOperatorDataSource = getJoinExtraOperatorDataSource(exchangeVO);
			dataStreamSourceVOs.addAll(joinExtraOperatorDataSource);
		}
		if(dataStreamSourceVOs.size()!=2){
			throw new RuntimeException("数据流连接处理器数据源个数不等于2个，请检查！");
		}

		Integer startIndex=groupId.lastIndexOf("-");
		//分析器code
		String filterCode=groupId.substring(startIndex+1);

		// 获取规则
		FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(filterCode);

		 DataStreamSourceVO dataStreamSourceVO = new DataStreamSourceVO();
		 //获得输出的对应列的信息
		 List<FieldInfoVO> outputFieldInfoVOs = ExchangeUtil.getTargetFieldInfos(filterConfigObject, exchange,roomType);
		 DataStream<Row> joinDataStream = conversejoinDataStream(filterConfigObject, exchange, outputFieldInfoVOs,dataStreamSourceVOs,roomType,filterOperator.getDesc());
		 List<Attach> dataStreamAttach = ExchangeUtil.getDataStreamAttach(filterConfigObject, exchange.getTarget());
		 dataStreamSourceVO.setDataStreamSource(joinDataStream);
		 dataStreamSourceVO.setFieldInfoVOs(outputFieldInfoVOs);
		 dataStreamSourceVO.setSourceId(exchange.getTarget());
		 dataStreamSourceVO.setAttachs(dataStreamAttach);
		 return dataStreamSourceVO;
	}


	/**
	 * 转换获得对应的数据流
	 * @param filterConfigObject
	 * @param exchange
	 * @param outputFieldInfoVOs
	 * @param dataStreamSourceVOs
	 * @return
	 */
	private DataStream<Row> conversejoinDataStream(FilterConfigObject filterConfigObject, Exchanges exchange,
			List<FieldInfoVO> outputFieldInfoVOs, List<DataStreamSourceVO> dataStreamSourceVOs,String roomType,String fitlerCode) {
		 Attach attatch = ExchangeUtil.getAttatchInfos(filterConfigObject, exchange.getSources(),"window");
		 WindowConfig windowConfig = getJointWindowAttachOptions(attatch);
		 if(windowConfig!=null){
			 DataStream<Row> unionDataStream = getJoinDataStream(dataStreamSourceVOs,windowConfig,roomType);
			 unionDataStream=FilterOperatorUtil.convertDataStreamWithTypeInformation(unionDataStream,outputFieldInfoVOs,fitlerCode);
			 return unionDataStream;
		 }else {
			 throw new RuntimeException("join连接器没有配置时间窗，请检查！");
		 }
		 
	}

	
	/**
	 * 根据附件获得对应的时间类型
	 * @param attatch
	 * @return
	 */
	private WindowConfig getJointWindowAttachOptions(Attach attatch) {
		Gson gson = new Gson();
		if(attatch!=null){
			if(attatch.getType().equals("window")){
				String options = attatch.getOptions();
				WindowConfig windowConfig = gson.fromJson(options, WindowConfig.class);
				return windowConfig;
			}
			throw new RuntimeException("数据流连接处理器附必须是window类型，请检查！");
		}
		return null;
	}
	
	/**
	 * 数据流配置出对应的类型
	 * @param outTypeInformationTypes
	 * @param unionDataStream
	 * @return
	 */
	private DataStream<Row> convertDataStreamWithTypeInformation(TypeInformation<Row> outTypeInformationTypes,
			DataStream<Row> unionDataStream) {
		unionDataStream = unionDataStream.map(new MapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Row map(Row value) throws Exception {
				return value;
			}
		}).returns(outTypeInformationTypes);
		return unionDataStream;
	}

	
	
	
	
	/**
	 * 获得join的数据源
	 * @param dataStreamSourceVOs
	 * @return
	 */
	private DataStream<Row> getJoinDataStream(List<DataStreamSourceVO> dataStreamSourceVOs,WindowConfig windowConfig,String roomType) {
		//获得单位时间
		
		DataStreamSourceVO initdataStreamSourceVO = dataStreamSourceVOs.get(0);
		DataStreamSourceVO otherStreamSourceVO = dataStreamSourceVOs.get(1);
		
		DataStream<Row> initDataStreamSource = initdataStreamSourceVO.getDataStreamSource();
		List<Attach> initattachs = initdataStreamSourceVO.getAttachs();
		List<FieldInfoVO> initFieldInfoVOs = initdataStreamSourceVO.getFieldInfoVOs();
		Integer joinRowOrder = getJoinRowKeyOrder(initattachs, initFieldInfoVOs);
		
		DataStream<Row> otherDataStream = otherStreamSourceVO.getDataStreamSource();
		List<Attach> otherattachs = otherStreamSourceVO.getAttachs();
		List<FieldInfoVO> otherFieldInfoVOs = otherStreamSourceVO.getFieldInfoVOs();
		Integer otherRowOrder = getJoinRowKeyOrder(otherattachs, otherFieldInfoVOs);
        
		MapOrder initIdRoomOrder = ExchangeUtil.getRoomOrder(initFieldInfoVOs,roomType);
		MapOrder otherIdRoomOrder = ExchangeUtil.getRoomOrder(otherFieldInfoVOs,roomType);
		
		//获得重复的数据
		List<FieldInfoVO> repeatFieldInfoVOs = FieldInfoUtil.getRepeatFieldInfoVOsByFieldName(initFieldInfoVOs, otherFieldInfoVOs);
		
		
		String type = windowConfig.getType();
		WithWindow<Row, Row, String, ?> windowStream = getWindowStream(initDataStreamSource, otherDataStream, 
				initFieldInfoVOs,otherFieldInfoVOs, windowConfig, type, joinRowOrder, otherRowOrder);
		initDataStreamSource = windowStream.apply(new JoinFunction<Row, Row, Row>() {
			
			       private static final long serialVersionUID = 1L;
					@Override
					public Row join(Row first, Row second) throws Exception {
						second = getTranformNewRow(second, repeatFieldInfoVOs);
						int arity1 = first.getArity();
						int arity2 = second.getArity();
						Row newRow = new Row(arity1+arity2);
						int i = 0;
						for (i = 0; i < arity1; i++) {
							newRow.setField(i, first.getField(i));
						}
						for (int j = 0; j < arity2; j++,i++) {
							newRow.setField(i, second.getField(j));
						}
						Row mapRow = getRoomRow(initIdRoomOrder, otherIdRoomOrder, arity1, arity2, newRow,roomType);
						return mapRow;
					}
				
				});
		return initDataStreamSource;
	}

	
	/**
	 * 获得旋转完成以后的新的row，将重复字段放到后面
	 * @param row
	 * @param repeatFieldInfoVO
	 * @return
	 */
	private   Row  getTranformNewRow(Row row,List<FieldInfoVO> repeatFieldInfoVO) {
		//TODO 没有重复的元素
		int arity = row.getArity();
		if(repeatFieldInfoVO.size()==0) {
			return row;  
		}
		if(arity==0) {
			return row;
		}
		Row midRow = new Row(arity+repeatFieldInfoVO.size()); //中间转换的row
		Row lastRow = new Row(arity);
		int i = 0;
		for (i = 0; i < arity-1; i++) {
			midRow.setField(i, row.getField(i));
		}
		for (int j = 0; j <repeatFieldInfoVO.size(); j++,i++) {
			FieldInfoVO fieldInfoVO = repeatFieldInfoVO.get(j);
			midRow.setField(i, row.getField(fieldInfoVO.getOrder()));
		}
		midRow.setField(midRow.getArity()-1, row.getField(arity-1));
		
		List<Integer> list = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : repeatFieldInfoVO) {
			list.add(fieldInfoVO.getOrder());
		}
		
		for (int k = 0,j = 0; k < arity+2; k++) {
			if(!list.contains(k)) {
				lastRow.setField(j, midRow.getField(k));
				j++;
			}
		}
		
		return lastRow;
	}
	
	
	/**
	 * 获得旋转完成以后的新的row，将重复字段放到后面
	 * Row row,List<FieldInfoVO> repeatFieldInfoVO
	 * @return
	 */
	private static void getTranformNewRowTest() {
		Row row1 = Row.of(1,2,3,4,5,6,7,8);
		//1,2,3,4,5,6,7,8-------->1,2,3,4,5,6,7,2,6,8------->1,3,4,5,7,2,6,8
		int arity = row1.getArity();
		Row row2 = new Row(arity+2);
		Row row3 = new Row(arity);
		int pos1 = 1;
		int pos2 = 5;
		
		List<Object> fieldList = new ArrayList<>();
		Object field1 = row1.getField(pos1);
		Object field2 = row1.getField(pos2);
		fieldList.add(field1);
		fieldList.add(field2);
		int i = 0;
		for (i = 0; i < arity-1; i++) {
			row2.setField(i, row1.getField(i));
		}
		for (int j = 0; j <fieldList.size(); j++,i++) {
			row2.setField(i, fieldList.get(j));
		}
		row2.setField(row2.getArity()-1, row1.getField(arity-1));
		
		
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(5);
		for (int k = 0,j = 0; k < arity+2; k++) {
			if(!list.contains(k)) {
				row3.setField(j, row2.getField(k));
				j++;
			}
		}
		System.out.println(row3);
		
	}
	
	public static void main(String[] args) {
		getTranformNewRowTest();
	}
	
	
	/**
	 * 获得关联RoomId的row
	 * @param initIdRoomOrder
	 * @param otherIdRoomOrder
	 * @param arity1
	 * @param arity2
	 * @param newRow
	 * @return
	 */
	private Row getRoomRow(MapOrder initIdRoomOrder, MapOrder otherIdRoomOrder, int arity1,
			int arity2, Row newRow,String roomType) {
		Row mapNotRow = new Row(arity1+arity2-2); //去掉roomId
		Integer initorder = initIdRoomOrder.getOrder();
		Integer otherorder =arity1+otherIdRoomOrder.getOrder();
		for (int i = 0; i < initorder; i++) {
			mapNotRow.setField(i, newRow.getField(i));
		}
		
		for (int j = initorder+1; j < otherorder; j++) {
			mapNotRow.setField(j-1, newRow.getField(j));
		}
		
		Row mapRow = new Row(arity1+arity2-1);
		for (int i = 0; i < mapNotRow.getArity(); i++) {
			mapRow.setField(i, mapNotRow.getField(i));
		}
		Object initObject = newRow.getField(initorder);
		Object otherObject = newRow.getField(otherorder);
		if(initObject instanceof Map<?, ?> && otherObject instanceof Map<?, ?>){
			switch (roomType) {
			case RoomInfoConstant.ID_ROOM_TYPE:
				Map<String, String[]> idInMap = (Map<String, String[]>)initObject;
				Map<String, String[]> idOutMap = (Map<String, String[]>)otherObject;
				idInMap.putAll(idOutMap);
				mapRow.setField(mapRow.getArity()-1, idInMap);
				break;
			case RoomInfoConstant.TIME_ROOM_TYPE:
				Map<String, Map<String,String>> timeInputMap = (Map<String, Map<String,String>>)initObject;
				Map<String, Map<String,String>> timeOutMap = (Map<String, Map<String,String>>)otherObject;
				timeInputMap.putAll(timeOutMap);
				mapRow.setField(mapRow.getArity()-1, timeInputMap);
				break;
			default:
				break;
			}
		}else {
			throw new RuntimeException("对应row的值不是map类型请检查！");
		}
		return mapRow;
	}
	
	
	
	/**
	 * 获得连接joinRow
	 * @param attachs
	 * @return
	 */
	private Integer getJoinRowKeyOrder(List<Attach> attachs,List<FieldInfoVO> fieldInfoVOs){
		for (Attach attach : attachs) {
			String type = attach.getType();
			if(type.equals("key")){
				String options = attach.getOptions();
				String[] split = options.split(",");
				if(split.length==1){
					for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
						String fieldName = fieldInfoVO.getFieldName();
						if(options.equals(fieldName)){
							Integer order = fieldInfoVO.getOrder();
							return order;
						}
					}
					throw new RuntimeException("join处理器没有对应的数据源，请检查！");
				}else {
					throw new RuntimeException("join处理器的key只能有一个，请检查！,现在有"+split.length+"个");
				}
			}
		}
		
		throw new RuntimeException("attachs当中没有key类型的附件，请检查！");
	}


	/**
	 * 获得数据流连接器额外数据流
	 * @param exchangeVO
	 * @return
	 */
	private List<DataStreamSourceVO> getJoinExtraOperatorDataSource(ExchangeVO exchangeVO) {
		//TODO 1.目前只有一个数据源，但是exchange可能里面含有另外一个source是evenTable类型(先完成)，或者是filter过滤器的应用，或者是innertable
		//2.如果不包含，则抛出异常，该数据流只有一个，无法进行join连接
		List<DataStreamSourceVO> dataStreamSourceVOs = exchangeVO.getDataStreamSourceVOs();
		Exchanges exchanges = exchangeVO.getExchanges();
		StreamExecutionEnvironment env = exchangeVO.getEnv();
		FilterConfigObject filterConfigObject = exchangeVO.getFilterConfigObject();
		String roomType = exchangeVO.getRoomType();
		String groupId = exchangeVO.getGroupId();
		String startConfig = exchangeVO.getStartConfig();
		List<String> sources = exchanges.getSources();
		List<String> newSource = new ArrayList<>();
		if(sources.size()<2){
			throw new RuntimeException("数据流连接处理器数据源个数必须大于2个，请检查！");
		}else {
			DataStreamSourceVO dataStreamExistSourceVO = dataStreamSourceVOs.get(0); //已经存在
			String existSourceId = dataStreamExistSourceVO.getSourceId();
			Iterator<String> sourcesIterator = sources.iterator();
			while(sourcesIterator.hasNext()){
				String sourceId = sourcesIterator.next();
				if(!sourceId.equals(existSourceId)){
					newSource.add(sourceId);
				}
			}

			DataSourceInputParam dataSourceInputParam = FilterOperatorUtil.getDataSourceInputParam(exchangeVO);
			List<DataStreamSourceVO> list = 	GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
			return list;
		}
	}

	
	
	/**
	 * 获得对应的window属性流
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param type
	 * @return
	 */
	private WithWindow<Row, Row, String, ?> getWindowStream(DataStream<Row> initDataStreamSource,DataStream<Row> otherStream, 
			List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> otherFieldInfos,
			WindowConfig windowConfig, String type,Integer joinRowOrder,Integer otherRowOrder) {
		WithWindow<Row, Row, String, ?> window = null;
		switch (type) {
		case "time":
			window=getTimeWindow(initDataStreamSource, otherStream, inputFieldInfos,otherFieldInfos, windowConfig, joinRowOrder, otherRowOrder, window);
			break;
		case "count":
			window=getCountWindow(initDataStreamSource, otherStream, inputFieldInfos, windowConfig, joinRowOrder, otherRowOrder, window);
			break;
		case "session": 
			window=getSessionWindow(initDataStreamSource, otherStream, inputFieldInfos,otherFieldInfos, windowConfig, joinRowOrder, otherRowOrder, window);
			break;
		default:
			break;
		}
		return window;
	}
    

	private WithWindow<Row, Row, String, ?> getCountWindow(DataStream<Row> initDataStreamSource,DataStream<Row> otherStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig,Integer joinRowOrder,Integer otherRowOrder, WithWindow<Row, Row, String, ?> window){
		JoinedStreams<Row, Row>.Where<String>.EqualTo joinDataStream = initDataStreamSource.join(otherStream)
				.where(new KeySelectorFunction(joinRowOrder)).equalTo(new KeySelectorFunction(otherRowOrder));
		Long count = windowConfig.getCount();
		Long countSlide = windowConfig.getCountSlide();
		if(count==countSlide) {
			window=joinDataStream.window(GlobalWindows.create()).trigger(PurgingTrigger.of(CountTrigger.of(count)));
		}else {
			window=joinDataStream.window(GlobalWindows.create())
			.evictor(CountEvictor.of(count)).trigger(CountTrigger.of(countSlide));
		}
		return window;
	}
	
	

	/**
	 * 获得session的window窗ProcessingTimeSessionWindows,EventTimeSessionWindows
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param window
	 */
	private WithWindow<Row, Row, String, ?> getSessionWindow(DataStream<Row> initDataStreamSource,DataStream<Row> otherStream
			, List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> otherFieldInfos,
			WindowConfig windowConfig,Integer joinRowOrder,Integer otherRowOrder, WithWindow<Row, Row, String, ?> window) {
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		String timeAttr = windowConfig.getTimeAttr();
		JoinedStreams<Row, Row>.Where<String>.EqualTo joinDataStream = null;
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			joinDataStream = initDataStreamSource.join(otherStream)
			.where(new KeySelectorFunction(joinRowOrder)).equalTo(new KeySelectorFunction(otherRowOrder));
			window = joinDataStream.window(ProcessingTimeSessionWindows.withGap(time));
			break;
		case "eventTime":
			initDataStreamSource = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(initDataStreamSource, inputFieldInfos);
			otherStream = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(otherStream, otherFieldInfos);
			joinDataStream = initDataStreamSource.join(otherStream)
					.where(new KeySelectorFunction(joinRowOrder)).equalTo(new KeySelectorFunction(otherRowOrder));
			window=joinDataStream.window(EventTimeSessionWindows.withGap(time));
			break;
		default:
			throw new RuntimeException("timeArr没有赋值，没有指定对应的时间类型，请检查！");
		}
		return window;
	
	}
	
	/**
	 * 获得时间的window窗
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param window
	 */
	private WithWindow<Row, Row, String, ?> getTimeWindow(DataStream<Row> initDataStreamSource,DataStream<Row> otherStream
			, List<FieldInfoVO> inputFieldInfos,List<FieldInfoVO> otherFieldInfos,
			WindowConfig windowConfig,Integer joinRowOrder,Integer otherRowOrder, WithWindow<Row, Row, String, ?> window) {
		
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		Time timeSlide = ExchangeUtil.getTimeUnit(windowConfig.getTimeSlideValue(), windowConfig.getTimeSlideUnit());
		Long timeValue = windowConfig.getTimeValue();  // 窗口大小
		Long timeSlideValue = windowConfig.getTimeSlideValue();
		String timeAttr = windowConfig.getTimeAttr();
		JoinedStreams<Row, Row>.Where<String>.EqualTo joinDataStream = null;
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			joinDataStream = initDataStreamSource.join(otherStream)
			.where(new KeySelectorFunction(joinRowOrder)).equalTo(new KeySelectorFunction(otherRowOrder));
			if(timeValue==timeSlideValue){
				window = joinDataStream.window(TumblingProcessingTimeWindows.of(time));
			}else {
				window =  joinDataStream.window(SlidingProcessingTimeWindows.of(time, timeSlide));
			}				
			break;
		case "eventTime":
			initDataStreamSource = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(initDataStreamSource, inputFieldInfos);
			otherStream = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(otherStream, otherFieldInfos);
			joinDataStream = initDataStreamSource.join(otherStream)
					.where(new KeySelectorFunction(joinRowOrder)).equalTo(new KeySelectorFunction(otherRowOrder));
			if(timeValue==timeSlideValue){
				window=joinDataStream.window(TumblingEventTimeWindows.of(time));
			}else {
				window=joinDataStream.window(SlidingEventTimeWindows.of(time, timeSlide));
			}		
			break;
		default:
			throw new RuntimeException("timeArr没有赋值，没有指定对应的时间类型，请检查！");
		}
		return window;
	}



	
	
	
	
}
