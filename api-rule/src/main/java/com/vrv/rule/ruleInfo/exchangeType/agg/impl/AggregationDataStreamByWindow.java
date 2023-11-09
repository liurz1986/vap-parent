package com.vrv.rule.ruleInfo.exchangeType.agg.impl;

import java.io.Serializable;
import java.util.List;

import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

import com.vrv.rule.model.WindowConfig;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.exchangeType.agg.AcculateAggregteFuntion;
import com.vrv.rule.ruleInfo.exchangeType.agg.AggregationOperatorExecutor;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 根据window进行聚合操作
 * @author wd-pc
 *
 */
public class AggregationDataStreamByWindow extends AggregationOperatorExecutor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> aggByDataStream(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			List<FieldInfoVO> outputFieldInfos,WindowConfig windowConfig,String roomType,String... keys) {
		String type = windowConfig.getType();
		AllWindowedStream<Row, ?> windowStream = getWindowStream(dataStream, inputFieldInfos, windowConfig, type);
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
	private AllWindowedStream<Row, ?> getWindowStream(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, String type) {
		AllWindowedStream<Row, ?> window = null;
		switch (type) {
		case "global":
			window = dataStream.windowAll(GlobalWindows.create());
			break;
		case "time":
			window = getTimeWindow(dataStream, inputFieldInfos, windowConfig, window);
			break;
		case "count":
			window = getCountWindow(dataStream, windowConfig,window);
			break;
		case "session":  //TODO session不支持fold需要用多态重新写
			window = getSessionWindow(dataStream, inputFieldInfos, windowConfig, window);
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
	private AllWindowedStream<Row, ?> getCountWindow(DataStream<Row> dataStream, WindowConfig windowConfig,AllWindowedStream<Row, ?> window) {
		Long count = windowConfig.getCount();
		Long countSlide = windowConfig.getCountSlide();
		if(count==countSlide){
			window = dataStream.countWindowAll(count);
		}else {
			window=dataStream.countWindowAll(count, countSlide);
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
	private AllWindowedStream<Row, ?> getSessionWindow(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, AllWindowedStream<Row, ?> window) {
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		String timeAttr = windowConfig.getTimeAttr();
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			window =  dataStream.windowAll(ProcessingTimeSessionWindows.withGap(time));
			break;
		case "eventTime":
			dataStream = ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(dataStream, inputFieldInfos);
			window=dataStream.windowAll(EventTimeSessionWindows.withGap(time));
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
	private AllWindowedStream<Row, ?> getTimeWindow(DataStream<Row> dataStream, List<FieldInfoVO> inputFieldInfos,
			WindowConfig windowConfig, AllWindowedStream<Row, ?> window) {
		Time time = ExchangeUtil.getTimeUnit(windowConfig.getTimeValue(),windowConfig.getTimeUnit());
		Time timeSlide = ExchangeUtil.getTimeUnit(windowConfig.getTimeSlideValue(), windowConfig.getTimeSlideUnit());
		Long timeValue = windowConfig.getTimeValue();  // 窗口大小
		Long timeSlideValue = windowConfig.getTimeSlideValue();
		String timeAttr = windowConfig.getTimeAttr();
		switch (timeAttr) {
		case "processTime":
		case "ingestionTime":
			if(timeValue==timeSlideValue){
				window =  dataStream.windowAll(TumblingProcessingTimeWindows.of(time));
			}else {
				window =  dataStream.windowAll(SlidingProcessingTimeWindows.of(time, timeSlide));
			}				
			break;
		case "eventTime":
			dataStream=ExchangeUtil.dataStreamAssignTimestampAndWatermarksByBound(dataStream, inputFieldInfos);
			if(timeValue==timeSlideValue){
				window=dataStream.windowAll(TumblingEventTimeWindows.of(time));
			}else {
				window=dataStream.windowAll(SlidingEventTimeWindows.of(time, timeSlide));
			}		
			break;
		default:
			break;
		}
		return window;
	}
	
	
	

}
