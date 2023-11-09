package com.flink.demo.vo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.table.api.Types;
import org.apache.flink.types.Row;

import com.flink.demo.analysis.FilterOperatorModel;
import com.vrv.logVO.monior.IfEntry;
import com.vrv.logVO.monior.RunningDetails;
import com.vrv.logVO.monior.SwitchVo;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.vo.AggregateOperator;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.FieldTypeEnum;
import com.vrv.rule.vo.LogicOperator;
import com.vrv.rule.vo.SwitchTableVO;

/**
 * @author wudi E-mail:wudi891012@163.com
 * @version 创建时间：2018年10月26日 下午3:20:35 类说明
 */
public class POJOTestVO {

	public static List<WordCountVO> getPojoTest(){
		List<WordCountVO> list = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			WordCountVO wordCountVO = new WordCountVO();
			wordCountVO.setWord("wudi"+i);
			wordCountVO.setCount(i);
			list.add(wordCountVO);
		}
		return list;
	}
	
	public static List<Map<String,Object>> getSwitchInfoByMap(){
		List<Map<String,Object>> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Map<String,Object> map = new HashMap<>();
			map.put("collectorIp", "64.97.130.1");
			map.put("assetGuid", UUID.randomUUID().toString());
			map.put("counts", 8);
			list.add(map);
		}
		return list;
	}
	
	
	
	public static List<SwitchVo> getSwitch(){
		List<SwitchVo> list = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			SwitchVo switchTestVO = new SwitchVo();
			switchTestVO.setAssetGuid(UUID.randomUUID().toString());
			switchTestVO.setIfNumber(10);
			switchTestVO.setCollectorIp("64.97.130.1");
			switchTestVO.setTriggerTime(new Timestamp(new Date().getTime()));
			//switchTestVO.setCounts(8);
			RunningDetails runningDetailsTest = new RunningDetails();
			runningDetailsTest.setActiveSessions("1000");
			runningDetailsTest.setBytesReceived("1000");
			runningDetailsTest.setBytesSent("10000");
			runningDetailsTest.setStartTime(DateUtil.format(new Date()));
			switchTestVO.setRunningDetails(runningDetailsTest);
			List<IfEntry> entryList = new ArrayList<>();
			for (int j = 0; j < 10; j++) {
				IfEntry ifEntryTest = new IfEntry();
				ifEntryTest.setIfMtu("1500"+j);
				ifEntryTest.setIfDescr("desc");
				ifEntryTest.setIfIndex("200"+j);
				ifEntryTest.setIfType("switch");
				entryList.add(ifEntryTest);
			}
			IfEntry[] ifEntryArr = entryList.toArray(new IfEntry[entryList.size()]);
			switchTestVO.setIfEntryList(ifEntryArr);
			list.add(switchTestVO);
		}
		return list;
	}
	
	
	/**
	 * 获得inputTest
	 * @return
	 */
	public static List<Tuple3<Integer, String, Integer>> inputTest(){
		final List<Tuple3<Integer, String, Integer>> input = new ArrayList<>();
		input.add(new Tuple3<>(1, "a", 1));
		input.add(new Tuple3<>(1, "a", 1));
		input.add(new Tuple3<>(2, "a", 1));
		input.add(new Tuple3<>(2, "b", 1));
		input.add(new Tuple3<>(3, "b", 1));
		input.add(new Tuple3<>(3, "c", 1));
		input.add(new Tuple3<>(1, "c", 1));
		input.add(new Tuple3<>(3, "c", 1));
		return input;
	}
	
	public static List<SwitchTableVO> getRow(){
		List<SwitchTableVO> rows = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			SwitchTableVO switchTableVO = new SwitchTableVO();
			switchTableVO.setAssetGuid(UUID.randomUUID().toString());
			switchTableVO.setIfNumber(UUID.randomUUID().toString());
			switchTableVO.setCollectorIp("64.97.130.1");
			switchTableVO.setTriggerTime(new Timestamp(new Date().getTime()));
			switchTableVO.setCounts(9);
			rows.add(switchTableVO);
		}
		return rows;
	}
	
	public static DataStream<Row> getRows(LocalStreamEnvironment localEnvironment){
		List<Row> rows = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			IfEntry[] ifEntry = getIfEntry();
			Row row = Row.of(UUID.randomUUID().toString(),UUID.randomUUID().toString(),"64.97.130.1",new Timestamp(new Date().getTime()),10,ifEntry);
			rows.add(row);
		}
		
		List<FieldInfoVO> ifEntryList = getIfEntryList();
		TypeInformation[] typeInformationTypes = getTypeInformation(ifEntryList);
		
		String[] switchField = {"assetGuid","ifNumber","collectorIp","triggerTime","counts","ifEntry"};
		TypeInformation<?>[] types = new TypeInformation[]{Types.STRING(),Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP(), Types.OBJECT_ARRAY(Types.ROW(typeInformationTypes))};
		TypeInformation<Row> row = Types.ROW(switchField, types);
		DataStreamSource<Row> dataStreamSource = localEnvironment.fromCollection(rows,row);
		return dataStreamSource;
		
	}

	/**
	 * 获得对应端口的数据
	 * @return
	 */
	private static IfEntry[] getIfEntry() {
		List<IfEntry> entryList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			IfEntry ifEntry = new IfEntry();
			ifEntry.setIfDescr("Switch"+Math.random());
			ifEntry.setIfMtu("1000"+Math.random());
			entryList.add(ifEntry);
			
		}
		IfEntry[] arr = entryList.toArray(new IfEntry[entryList.size()]);
		return arr;
	}
	/**
	 * 获得Pojo相关的数据
	 * @return
	 */
	public static List<FieldInfoVO> getPojoRunningDetailVO(){
		List<FieldInfoVO> list = new ArrayList<>();

		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setFieldName("bytesSent");
		assetGuidFieldInfo.setFieldType("varchar");
		list.add(assetGuidFieldInfo);
		
		FieldInfoVO ifNumberFieldInfo = new FieldInfoVO(); 
		ifNumberFieldInfo.setFieldName("currentThreadCount");
		ifNumberFieldInfo.setFieldType("varchar");
		list.add(ifNumberFieldInfo);
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("currentThreadsBusy");
		collectorIpFieldInfo.setFieldType("varchar");
		list.add(collectorIpFieldInfo);
		return list;
	}
	
	/**
	 * 获得维表信息
	 * @return
	 */
	public static List<FieldInfoVO> getDimensionInputFieldInfoList(){
		List<FieldInfoVO> list = new ArrayList<>();
		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setTableName("a");
		assetGuidFieldInfo.setFieldName("assetGuid");
		assetGuidFieldInfo.setFieldType("varchar");
		assetGuidFieldInfo.setOrder(0);
		list.add(assetGuidFieldInfo);
		
		FieldInfoVO ifNumberFieldInfo = new FieldInfoVO();
		ifNumberFieldInfo.setTableName("b");
		ifNumberFieldInfo.setFieldName("ifNumber");
		ifNumberFieldInfo.setFieldType("varchar");
		ifNumberFieldInfo.setOrder(1);
		list.add(ifNumberFieldInfo);
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		collectorIpFieldInfo.setOrder(2);
		list.add(collectorIpFieldInfo);
		return list;
	}
	
	
	
	/**
	 * 获得维表信息
	 * @return
	 */
	public static List<FieldInfoVO> getDimensionOutputFieldInfoList(){
		List<FieldInfoVO> list = new ArrayList<>();
		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setTableName("a");
		assetGuidFieldInfo.setFieldName("assetGuid");
		assetGuidFieldInfo.setFieldType("varchar");
		assetGuidFieldInfo.setOrder(0);
		list.add(assetGuidFieldInfo);
		
		FieldInfoVO ifNumberFieldInfo = new FieldInfoVO();
		ifNumberFieldInfo.setTableName("b");
		ifNumberFieldInfo.setFieldName("ifNumber");
		ifNumberFieldInfo.setFieldType("varchar");
		ifNumberFieldInfo.setOrder(1);
		list.add(ifNumberFieldInfo);
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		collectorIpFieldInfo.setOrder(2);
		list.add(collectorIpFieldInfo);
		
		
		FieldInfoVO assetGuidFieldInfos = new FieldInfoVO(); 
		assetGuidFieldInfos.setFieldName("guid");
		assetGuidFieldInfos.setFieldType("varchar");
		assetGuidFieldInfos.setOrder(3);
		list.add(assetGuidFieldInfos);
		
		FieldInfoVO assetIpFieldInfo = new FieldInfoVO(); 
		assetIpFieldInfo.setFieldName("ip");
		assetIpFieldInfo.setFieldType("varchar");
		assetIpFieldInfo.setOrder(4);
		list.add(assetIpFieldInfo);
		
		
		return list;
	}
	
	
	
	
	public static List<FieldInfoVO> getFieldInfoList(){
		List<FieldInfoVO> list = new ArrayList<>();

		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setTableName("a");
		assetGuidFieldInfo.setFieldName("assetGuid");
		assetGuidFieldInfo.setFieldType("varchar");
		assetGuidFieldInfo.setOrder(0);
		list.add(assetGuidFieldInfo);
		
		FieldInfoVO ifNumberFieldInfo = new FieldInfoVO();
		ifNumberFieldInfo.setTableName("b");
		ifNumberFieldInfo.setFieldName("ifNumber");
		ifNumberFieldInfo.setFieldType("varchar");
		ifNumberFieldInfo.setOrder(1);
		list.add(ifNumberFieldInfo);
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		collectorIpFieldInfo.setOrder(2);
		list.add(collectorIpFieldInfo);
		
		FieldInfoVO triggerTimeFieldInfo = new FieldInfoVO(); 
		triggerTimeFieldInfo.setFieldName("triggerTime");
		triggerTimeFieldInfo.setFieldType("datetime");
		triggerTimeFieldInfo.setOrder(3);
		list.add(triggerTimeFieldInfo);
		
		FieldInfoVO countsFieldInfo = new FieldInfoVO(); 
		countsFieldInfo.setFieldName("counts");
		countsFieldInfo.setFieldType("bigint");
		countsFieldInfo.setOrder(4);
		list.add(countsFieldInfo);
		
		
		FieldInfoVO speedRateFieldInfo = new FieldInfoVO(); 
		speedRateFieldInfo.setFieldName("speedRate");
		speedRateFieldInfo.setFieldType("bigint");
		speedRateFieldInfo.setOrder(5);
		list.add(speedRateFieldInfo);
		
	
		FieldInfoVO ifEntryFieldInfo = new FieldInfoVO(); 
		ifEntryFieldInfo.setFieldName("ifEntryList");
		ifEntryFieldInfo.setFieldType("pojoArray");
		List<FieldInfoVO> entryLists = getIfEntryList();
		ifEntryFieldInfo.setChildFields(entryLists);
		ifEntryFieldInfo.setOrder(6);
		list.add(ifEntryFieldInfo);
		
		
		FieldInfoVO pojoFieldInfo = new FieldInfoVO(); 
		pojoFieldInfo.setFieldName("runningDetail");
		pojoFieldInfo.setFieldType("pojo");
		List<FieldInfoVO> pojoRunningDetailVO = getPojoRunningDetailVO();
		pojoFieldInfo.setChildFields(pojoRunningDetailVO);
		pojoFieldInfo.setOrder(7);
		list.add(pojoFieldInfo);
		
		
		FieldInfoVO baseArrayFieldInfo = new FieldInfoVO(); 
		baseArrayFieldInfo.setFieldName("portArray");
		baseArrayFieldInfo.setFieldType("stringArray");
		baseArrayFieldInfo.setOrder(8);
		list.add(baseArrayFieldInfo);
		
		FieldInfoVO countArrayFieldInfo = new FieldInfoVO(); 
		countArrayFieldInfo.setFieldName("countArray");
		countArrayFieldInfo.setFieldType("longArray");
		countArrayFieldInfo.setOrder(9);
		list.add(countArrayFieldInfo);
		
		
		
		return list;
		
	}
	
	
	public static List<FieldInfoVO> getBaseArrayFieldInfo(){
		List<FieldInfoVO> list = new ArrayList<>();
		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setFieldName(null);
		assetGuidFieldInfo.setFieldType("int");
		list.add(assetGuidFieldInfo);
		return list;
	}
	
	
	private static TypeInformation<Row> getTypeInformationTypes(List<FieldInfoVO> fieldInfoList) {
		String[] field = getField(fieldInfoList);
		TypeInformation[] types = getTypeInformation(fieldInfoList);
		TypeInformation<Row> row = Types.ROW(field, types);
		return row;
	}
	
	public static String[] getField(List<FieldInfoVO> fieldInfoList){
		List<String> fieldList = new ArrayList<>();
		for (FieldInfoVO fieldInfoVO : fieldInfoList) {
			String fieldName = fieldInfoVO.getFieldName();
			fieldList.add(fieldName);
		}
		String[] field=fieldList.toArray(new String[fieldList.size()]);
	    return field;
	}
	
	/**
	 * 获得对应的匹配类型
	 * @return
	 */
	 private static TypeInformation[] getTypeInformation(List<FieldInfoVO> fieldInfoList){
		 List<TypeInformation> list = new ArrayList<>();
		 for (FieldInfoVO fieldInfoVO : fieldInfoList){
				String fieldType = fieldInfoVO.getFieldType();
				TypeInformation flinkType = FieldTypeEnum.getFlinkType(fieldType);
				list.add(flinkType);
		}
		 TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
		return types;
	 }
	
	
	 
	 public static List<FieldInfoVO> getIfEntryList(){
		 List<FieldInfoVO> list = new ArrayList<>();
			
			FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
			collectorIpFieldInfo.setFieldName("ifDescr");
			collectorIpFieldInfo.setFieldType("varchar");
			list.add(collectorIpFieldInfo);
			
			
			FieldInfoVO countsFieldInfo = new FieldInfoVO(); 
			countsFieldInfo.setFieldName("ifMtu");
			countsFieldInfo.setFieldType("varchar");
			list.add(countsFieldInfo);
			
			return list;
	 }
	 
	
	public static List<FieldInfoVO> getFieldInfoListFilter(){
		List<FieldInfoVO> list = new ArrayList<>();
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		list.add(collectorIpFieldInfo);
		
		
		FieldInfoVO countsFieldInfo = new FieldInfoVO(); 
		countsFieldInfo.setFieldName("counts");
		countsFieldInfo.setFieldType("bigint");
		list.add(countsFieldInfo);
		
		
		FieldInfoVO switchFieldInfo = new FieldInfoVO(); 
		switchFieldInfo.setFieldName("switchId");
		switchFieldInfo.setFieldType("varchar");
		list.add(switchFieldInfo);
		
		//TODO ifEntry字段
		FieldInfoVO ifEntryFieldInfo = new FieldInfoVO(); 
		ifEntryFieldInfo.setFieldName("ifEntry");
		ifEntryFieldInfo.setFieldType("varchar");
		list.add(ifEntryFieldInfo);
		
		//TODO POJO
		FieldInfoVO pojoFieldInfo = new FieldInfoVO(); 
		pojoFieldInfo.setFieldName("byteSends");
		pojoFieldInfo.setFieldType("varchar");
		list.add(pojoFieldInfo);
		
		
		return list;
		
		
		
	}
	
	
	
	public static List<FieldInfoVO> getFieldInfoListAggreat(){
		List<FieldInfoVO> list = new ArrayList<>();
		
		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
		assetGuidFieldInfo.setTableName("a");
		assetGuidFieldInfo.setFieldName("assetGuid");
		assetGuidFieldInfo.setFieldType("varchar");
		assetGuidFieldInfo.setOrder(0);
		list.add(assetGuidFieldInfo);
		
		
		
		FieldInfoVO totalFieldInfo = new FieldInfoVO(); 
		totalFieldInfo.setFieldName("total");
		totalFieldInfo.setFieldType("int");
		totalFieldInfo.setOrder(1);
		AggregateOperator aggregateOperator = new AggregateOperator();
		aggregateOperator.setOperator("count");
		LogicOperator logicFilter = FilterOperatorModel.getLogicFilter();
		aggregateOperator.setLoginExp(logicFilter);
		totalFieldInfo.setExpression(aggregateOperator);
		list.add(totalFieldInfo);
		
		
		FieldInfoVO ifNumberFieldInfo = new FieldInfoVO(); 
		ifNumberFieldInfo.setFieldName("ifNumber");
		ifNumberFieldInfo.setFieldType("varchar");
		ifNumberFieldInfo.setOrder(2);
		list.add(ifNumberFieldInfo);
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		collectorIpFieldInfo.setOrder(3);
		list.add(collectorIpFieldInfo);
		
		
		FieldInfoVO contatFieldInfo = new FieldInfoVO(); 
		contatFieldInfo.setFieldName("contatValue");
		contatFieldInfo.setFieldType("varchar");
		contatFieldInfo.setOrder(4);
		AggregateOperator contatOperator = new AggregateOperator();
		contatOperator.setOperator("concat");
		contatOperator.setField("speedRate");
		contatFieldInfo.setExpression(contatOperator);
		list.add(contatFieldInfo);
		
		
		
		FieldInfoVO sumFieldInfo = new FieldInfoVO(); 
		sumFieldInfo.setFieldName("sumValue");
		sumFieldInfo.setFieldType("bigint");
		sumFieldInfo.setOrder(5);
		AggregateOperator sumOperator = new AggregateOperator();
		sumOperator.setOperator("sum");
		sumOperator.setField("speedRate");
		sumFieldInfo.setExpression(sumOperator);
		list.add(sumFieldInfo);
		
		
		
		
		
		
		
		
//		FieldInfoVO assetGuidFieldInfo = new FieldInfoVO(); 
//		assetGuidFieldInfo.setFieldName("assetGuid");
//		assetGuidFieldInfo.setFieldType("varchar");
//		list.add(assetGuidFieldInfo);
		
		return list;
		
		
		
	}
	
	
	public static List<FieldInfoVO> getFieldInfoListFilterFinally(){
		List<FieldInfoVO> list = new ArrayList<>();
		
		FieldInfoVO collectorIpFieldInfo = new FieldInfoVO(); 
		collectorIpFieldInfo.setFieldName("collectorIp");
		collectorIpFieldInfo.setFieldType("varchar");
		collectorIpFieldInfo.setOrder(0);
		list.add(collectorIpFieldInfo);
		
		
		
		return list;
		
		
		
	}
	
	
	
}
