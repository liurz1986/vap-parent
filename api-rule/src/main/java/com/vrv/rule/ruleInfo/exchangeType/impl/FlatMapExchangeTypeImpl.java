package com.vrv.rule.ruleInfo.exchangeType.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.support.monitor.dao.MonitorDaoJdbcImpl.FieldInfo;
import com.google.gson.Gson;
import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.ruleInfo.exchangeType.flatMap.FlatRow;
import com.vrv.rule.ruleInfo.exchangeType.flatMap.RowInfo;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.source.TypeInformationClass;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 分解组件，主要针对的是结构化当中分解操作
 * @author wd-pc
 *
 */
public class FlatMapExchangeTypeImpl implements ExchangeType,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerFactory.getLogger(FlatMapExchangeTypeImpl.class);

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
			dataStreamSourceVOs = GetDataSourceStream.getDataStreamSource(env,dataSourceInputParam);
		}
		if(dataStreamSourceVOs.size()>1) { //说明有多个输入数据源
			throw new RuntimeException("分解处理器不存在多个输入数据源，请检查！");
		}

		Integer startIndex=groupId.lastIndexOf("-");
		//分析器code
		String filterCode=groupId.substring(startIndex+1);

		// 获取规则
		FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(filterCode);

		DataStreamSourceVO dataStreamNewSourceVO = new DataStreamSourceVO();
		DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
		DataStream<Row> dataStreamSource = dataStreamSourceVO.getDataStreamSource();
		List<FieldInfoVO> inputFieldInfoVOs = dataStreamSourceVO.getFieldInfoVOs();
		List<FieldInfoVO> outputFieldInfoVOs = ExchangeUtil.getTargetFieldInfos(filterConfigObject, exchange,roomType);
		List<FieldInfoVO> flatFieldInfo = getFlatFieldInfo(inputFieldInfoVOs);
		TypeInformation<Row> outTypeInformationTypes = TypeInformationClass.getTypeInformationTypes(flatFieldInfo);
		dataStreamSource=dataStreamSource.flatMap(new FlatMapFunction<Row, Row>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void flatMap(Row row, Collector<Row> out) throws Exception {
				List<Row> flatMapRow = getFlatMapRow(inputFieldInfoVOs, row);
				for (Row nrow : flatMapRow){
					out.collect(nrow);
				}
			}
		}).name(filterOperator.getDesc()).returns(outTypeInformationTypes);
		List<Attach> dataStreamAttach = ExchangeUtil.getDataStreamAttach(filterConfigObject, exchange.getTarget());
		dataStreamNewSourceVO.setDataStreamSource(dataStreamSource);
		if(flatFieldInfo.size()==outputFieldInfoVOs.size()){
			setFlatTableNameAndId(outputFieldInfoVOs, flatFieldInfo);
			dataStreamNewSourceVO.setFieldInfoVOs(flatFieldInfo);			
		}else{
			Gson gson = new Gson();
			List<String> flatNames = getFiledName(flatFieldInfo);
			List<String> outName = getFiledName(outputFieldInfoVOs);
			logger.info("flatNames:"+gson.toJson(flatNames));
			logger.info("outName:"+gson.toJson(outName));
			throw new RuntimeException("分解数据和平铺数据不一致，请检查！");
		}
		dataStreamNewSourceVO.setSourceId(exchange.getTarget());
		dataStreamNewSourceVO.setAttachs(dataStreamAttach);
		return dataStreamNewSourceVO;
	}


	/**
	 * 设置对应的tableId和TableName
	 * @param outputFieldInfoVOs
	 * @param flatFieldInfo
	 */
	private void setFlatTableNameAndId(List<FieldInfoVO> outputFieldInfoVOs, List<FieldInfoVO> flatFieldInfo) {
		for (int i = 0; i < flatFieldInfo.size(); i++) {
			String tableId = outputFieldInfoVOs.get(i).getTableId();
			String tableName = outputFieldInfoVOs.get(i).getTableName();
			flatFieldInfo.get(i).setTableId(tableId);
			flatFieldInfo.get(i).setTableName(tableName);
		}
	}


	private List<String> getFiledName(List<FieldInfoVO> flatFieldInfo) {
		List<String> flatList = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : flatFieldInfo) {
			String fieldName = fieldInfoVO.getFieldName();
			flatList.add(fieldName);
		}
		return flatList;
	}
	
	
	private List<FieldInfoVO> getFlatFieldInfo(List<FieldInfoVO> inputFields){
		RowInfo rowInfo = new RowInfo();
		rowInfo.setFields(inputFields);
		FlatRow flatRow= new FlatRow();
		flatRow.setRowInfo(rowInfo);
		List<FieldInfoVO> flatoutFields = flatRow.flatFields(0, "");
		return flatoutFields;
	}
	
	private List<Row> getFlatMapRow(List<FieldInfoVO> fields,Row inputRow){
		RowInfo rowInfo = new RowInfo();
		rowInfo.setFields(fields);
		FlatRow flatRow= new FlatRow();
		flatRow.setRowInfo(rowInfo);
		List<Row> list = flatRow.flatMap(inputRow,"");
		return list;
	}
	
	
	

}
