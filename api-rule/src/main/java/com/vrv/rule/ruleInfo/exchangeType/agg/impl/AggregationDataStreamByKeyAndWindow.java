package com.vrv.rule.ruleInfo.exchangeType.agg.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.types.Row;

import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.exchangeType.agg.AcculateAggregteFuntion;
import com.vrv.rule.ruleInfo.exchangeType.agg.AggregationOperatorExecutor;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 根据key进行聚合操作
 * @author wd-pc
 *
 */
public class AggregationDataStreamByKeyAndWindow extends AggregationOperatorExecutor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> aggByDataStream(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			List<FieldInfoVO> outputFieldInfos,WindowConfig windowConfig,String roomType, String... keys) {
		String type = windowConfig.getType();
		WindowedStream<Row,Tuple, ?> windowStream = getKeyWindowStream(dataStream, inputFieldInfos, windowConfig, type, keys);
		dataStream = windowStream.aggregate(new AcculateAggregteFuntion(outputFieldInfos,inputFieldInfos,roomType));
		return dataStream;
	
	}
	
	
	
	/**
	 * 获得对应的window属性流
	 * @param dataStream
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param type
	 * @return
	 */
	private WindowedStream<Row,Tuple, ?> getKeyWindowStream(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, String type,String... keys) {
		WindowedStream<Row,Tuple, ?> window = null;
		switch (type) {
		case "global": //TODO global(全局窗口，但凡有数据进入，就会触发计算)
			window = dataStream.keyBy(keys).window(GlobalWindows.create()).trigger(CountTrigger.of(0L));
			break;
		case "time":
			window = getTimeWindow(dataStream, inputFieldInfos, windowConfig, window,keys);
			break;
		case "count":
			window = getCountWindow(dataStream, windowConfig,window,keys);
			break;
		case "session":
			window = getSessionWindow(dataStream, inputFieldInfos, windowConfig, window,keys);
			break;	
		default:
			break;
		}
		return window;
	}
    
	/**
	 * 获得count的window
	 * @param dataStream
	 * @param windowConfig
	 */
	private WindowedStream<Row,Tuple, ?> getCountWindow(DataStream<Row> dataStream, WindowConfig windowConfig,WindowedStream<Row,Tuple, ?> window,String ...keys) {
		Long count = windowConfig.getCount();
		Long countSlide = windowConfig.getCountSlide();
		if(count==countSlide){
			window = dataStream.keyBy(keys).countWindow(count);
		}else {
			window=dataStream.keyBy(keys).countWindow(count, countSlide);
		}
		return window;
	}

	/**
	 * 获得session的window窗
	 * @param dataStream
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param window
	 */
	private WindowedStream<Row,Tuple, ?> getSessionWindow(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, WindowedStream<Row,Tuple, ?> window,String ... keys) {
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		String timeAttr = windowConfig.getTimeAttr();
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			window =  dataStream.keyBy(keys).window(ProcessingTimeSessionWindows.withGap(time));
			break;
		case "eventTime":
			dataStream = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(dataStream, inputFieldInfos);
			window=dataStream.keyBy(keys).window(EventTimeSessionWindows.withGap(time));
			break;
		default:
			break;
		}
		return window;
	}
	
	/**
	 * 获得时间的window窗
	 * @param dataStream
	 * @param inputFieldInfos
	 * @param windowConfig
	 * @param window
	 */
	private WindowedStream<Row,Tuple, ?> getTimeWindow(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, WindowedStream<Row,Tuple, ?> window,String ... keys) {
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		Time timeSlide = ExchangeUtil.getTimeUnit(windowConfig.getTimeSlideValue(), windowConfig.getTimeSlideUnit());
		Long timeValue = windowConfig.getTimeValue();  // 窗口大小
		Long timeSlideValue = windowConfig.getTimeSlideValue();
		String timeAttr = windowConfig.getTimeAttr();
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			if(timeValue==timeSlideValue){
				window =  dataStream.keyBy(keys).window(TumblingProcessingTimeWindows.of(time));
			}else {
				window =  dataStream.keyBy(keys).window(SlidingProcessingTimeWindows.of(time, timeSlide));
			}				
			break;
		case "eventTime":
			dataStream=ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(dataStream, inputFieldInfos);
			if(timeValue==timeSlideValue){
				window=dataStream.keyBy(keys).window(TumblingEventTimeWindows.of(time));
			}else {
				window=dataStream.keyBy(keys).window(SlidingEventTimeWindows.of(time, timeSlide));
			}		
			break;
		default:
			break;
		}
		return window;
	}
	
	
}
