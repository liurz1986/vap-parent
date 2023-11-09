package com.vrv.rule.ruleInfo.exchangeType.impl;

import java.util.List;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.vo.DataStreamInputVO;
import com.vrv.rule.vo.ExchangeVO;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.model.DataStreamSourceVO;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeType;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * map映射/过滤完成对应的转换(映射只有一个数据源)
 * @author wd-pc
 *
 */
public class MapExchangeTypeImpl implements ExchangeType {

	private static Logger logger = LoggerFactory.getLogger(MapExchangeTypeImpl.class);

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

		if(dataStreamSourceVOs.size()>1) { //说明有多个输入数据源
			throw new RuntimeException("映射/过滤处理器不存在多个输入数据源，请检查！");
		}

		Integer startIndex=groupId.lastIndexOf("-");
		//分析器code
		String filterCode=groupId.substring(startIndex+1);

		// 获取规则
		FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(filterCode);

		DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
		DataStream<Row> dataStreamSource = dataStreamSourceVO.getDataStreamSource();
		List<FieldInfoVO> inputFieldInfoVOs = dataStreamSourceVO.getFieldInfoVOs(); //输入数据类型
		List<FieldInfoVO> outputFieldInfoVOs = ExchangeUtil.getTargetFieldInfos(filterConfigObject, exchanges,roomType); //输出数据类型
		String inputTableName = FieldInfoUtil.getInputTableName(filterConfigObject, exchanges);
		String sql = ExchangeUtil.getDataStreamAnalysisSql(filterConfigObject, exchanges,roomType);
		DataStream<Row> dataStream = GetDataSourceStream.executeMapAndFilterFunctionBysql(env, dataStreamSource, inputFieldInfoVOs, outputFieldInfoVOs, sql, inputTableName,filterOperator.getDesc());
		List<Attach> dataStreamAttach = ExchangeUtil.getDataStreamAttach(filterConfigObject, exchanges.getTarget());
		DataStreamSourceVO dataStreamNewSourceVO = new DataStreamSourceVO();
		dataStreamNewSourceVO.setDataStreamSource(dataStream);
		dataStreamNewSourceVO.setFieldInfoVOs(outputFieldInfoVOs);
		dataStreamNewSourceVO.setSourceId(exchanges.getTarget());
		dataStreamNewSourceVO.setAttachs(dataStreamAttach);
		return dataStreamNewSourceVO;
	}


}
