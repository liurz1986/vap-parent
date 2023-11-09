package com.vrv.rule.ruleInfo.exchangeType.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 数据流拼接处理器
 * @author wd-pc
 *
 */
public class UnionExchangeTypeImpl implements ExchangeType,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public DataStreamSourceVO exchangeType(ExchangeVO exchangeVO) {
		List<DataStreamSourceVO> dataStreamSourceVOs = exchangeVO.getDataStreamSourceVOs();
		Exchanges exchanges = exchangeVO.getExchanges();
		StreamExecutionEnvironment env = exchangeVO.getEnv();
		FilterConfigObject filterConfigObject = exchangeVO.getFilterConfigObject();
		String roomType = exchangeVO.getRoomType();
		String groupId = exchangeVO.getGroupId();

		if(dataStreamSourceVOs.size()==0){ //说明为初始化数据从kafka（或者是过滤器引用）当中读取
			DataSourceInputParam dataSourceInputParam = FilterOperatorUtil.getDataSourceInputParam(exchangeVO);
			dataStreamSourceVOs = 	GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
		}
		if(dataStreamSourceVOs.size()==1){ //数据流连接处理器必须有两个的数据流，这个地方需要需要进行判断
			List<String> sources = exchanges.getSources();
			List<DataStreamSourceVO> joinExtraOperatorDataSource = getJoinExtraOperatorDataSource(exchangeVO);
			dataStreamSourceVOs.addAll(joinExtraOperatorDataSource);
		}
		if(dataStreamSourceVOs.size()<2){
			throw new RuntimeException("数据流拼接处理器数据源个数不等于2个，请检查！");
		}

		Integer startIndex=groupId.lastIndexOf("-");
		//分析器code
		String filterCode=groupId.substring(startIndex+1);

		// 获取规则
		FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(filterCode);

		 DataStreamSourceVO dataStreamSourceVO = new DataStreamSourceVO();
		 //获得输出的对应列的信息
		 List<FieldInfoVO> outputFieldInfoVOs = ExchangeUtil.getTargetFieldInfos(filterConfigObject, exchanges,roomType);
		 DataStream<Row> joinDataStream = converseUnionDataStream(filterConfigObject, exchanges, outputFieldInfoVOs,dataStreamSourceVOs,filterOperator.getDesc());
		 List<Attach> dataStreamAttach = ExchangeUtil.getDataStreamAttach(filterConfigObject, exchanges.getTarget());
		 dataStreamSourceVO.setDataStreamSource(joinDataStream);
		 dataStreamSourceVO.setFieldInfoVOs(outputFieldInfoVOs);
		 dataStreamSourceVO.setSourceId(exchanges.getTarget());
		 dataStreamSourceVO.setAttachs(dataStreamAttach);
		 return dataStreamSourceVO;
	}


	/**
	 * 转换获得对应的数据流
	 * @param filterConfigObject
	 * @param exchange
	 * @return
	 */
	private DataStream<Row> converseUnionDataStream(FilterConfigObject filterConfigObject, Exchanges exchange,
			List<FieldInfoVO> outputFieldInfoVOs, List<DataStreamSourceVO> dataStreamSourceVOs,String filterDesc) {
		if(dataStreamSourceVOs.size()>1){
			DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
			DataStream<Row> initdataStreamSource = dataStreamSourceVO.getDataStreamSource();
			for (int i = 1; i < dataStreamSourceVOs.size(); i++) {
				initdataStreamSource = initdataStreamSource.union(dataStreamSourceVOs.get(i).getDataStreamSource());
			}
			initdataStreamSource=FilterOperatorUtil.convertDataStreamWithTypeInformation(initdataStreamSource,outputFieldInfoVOs,filterDesc);
			return initdataStreamSource;
		}else {
			throw new RuntimeException("拼接数据流少于2，请检查！");
		}
		
		 
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
		List<String> sources = exchanges.getSources();
		StreamExecutionEnvironment env = exchangeVO.getEnv();
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
			List<DataStreamSourceVO> list = GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
			return list;
		}
	}





	
	
	
	
}
