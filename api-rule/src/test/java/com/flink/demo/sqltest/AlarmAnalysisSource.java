package com.flink.demo.sqltest;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.DefinedRowtimeAttributes;
import org.apache.flink.table.sources.RowtimeAttributeDescriptor;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.tsextractors.ExistingField;
import org.apache.flink.table.sources.wmstrategies.AscendingTimestamps;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.vo.OutPutVO;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月6日 上午11:51:21
* 类说明  基于事件时间告警分析
*/
public class AlarmAnalysisSource implements StreamTableSource<Row>,DefinedRowtimeAttributes {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static final String KAFKA_PRODUCER_URL  = "192.168.120.103:9092";
	public static final String TOPIC_PRODUCER_NAME = "flink-wiki-demo";
	public static final String KAFKA_CONSUMER_URL = "192.168.120.103:9092";
	public static final String TOPIC_CONSUMER_NAME = "flink-kafka";
	public static final String KAFKA_GROUP_ID = "test";
	
	@Override
	public String explainSource() {
		//TODO 流数据源说明
		return "AlarmAnalysisSource";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		TypeInformation<Row> types = getTypeInformationTypes();
		return types;
	}

	@Override
	public TableSchema getTableSchema() {
		return new TableSchema(
				new String[] {"related_ips","dst_ips","dst_ports","src_ips","src_ports","trigger_time"},
				new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()});
	}

	@Override
	public List<RowtimeAttributeDescriptor> getRowtimeAttributeDescriptors() {
		return Collections.
			   singletonList(new RowtimeAttributeDescriptor("trigger_time", 
					         new ExistingField("trigger_time"), 
					         new AscendingTimestamps())); 
	}

	@SuppressWarnings("deprecation")
	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		TypeInformation<Row> row = getTypeInformationTypes();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,new JsonRowDeserializationSchema(row) , properties));
		return addSource;
	}

	/**
	 * 获得TypeInformationTypes对应的信息
	 * @return
	 */
	private static TypeInformation<Row> getTypeInformationTypes() {
		String[] names = new String[] {"related_ips","dst_ips","dst_ports","src_ips","src_ports","trigger_time"};
		TypeInformation[] types = new TypeInformation[] {Types.STRING(), Types.STRING(),Types.STRING(), Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP()};
		TypeInformation<Row> row = Types.ROW(names, types);
		return row;
	}
	
	
//	  public static void main(String[] args) {
//		    String ruleCode = args[0];
//		    String source = args[1];
//		    String sql = args[2];
//			StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//			env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); //设置对应的时间类型
//			StreamTableEnvironment sTableEnv = TableEnvironment.getTableEnvironment(env);
//			
//	}
	
	
	
	
	public static void main(String[] args) throws Exception {	
		String ruleCode = "52";
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); //设置对应的时间类型
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		AlarmAnalysisSource alarmAnalysisSource = new AlarmAnalysisSource();
		Table table = sTableEnv.fromTableSource(alarmAnalysisSource);
		sTableEnv.registerTable("AlarmAnalysisSource", table);
		//select UserName,COUNT(URL) AS cnt,TUMBLE_END(UserActionTime,INTERVAL '1' HOUR) AS endT from UserActions group by UserName,TUMBLE(UserActionTime,INTERVAL '1' HOUR)
		String sql = "select dst_ips as ip,count(dst_ips) as num,TUMBLE_START(trigger_time,INTERVAL '5' SECOND) as entryTime,TUMBLE_END(trigger_time,INTERVAL '5' SECOND) " + 
				"as triggerTime from AlarmAnalysisSource where 1=1 group by dst_ips,TUMBLE(trigger_time,INTERVAL '5' SECOND) HAVING count(dst_ips)>2";
		Table sqlQuery = sTableEnv.sqlQuery(sql);
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,OutPutVO>> retractStream = sTableEnv.toRetractStream(sqlQuery, OutPutVO.class);
		//TODO 采用无结构化数据完成操作
		//TupleTypeInfo<Tuple3<String,String,Timestamp>> tupleTypeInfo = new TupleTypeInfo<>(Types.STRING(),Types.STRING(),Types.SQL_TIMESTAMP());
		//DataStream<Tuple2<Boolean,Tuple3<String,String,Timestamp>>> dataStream = sTableEnv.toRetractStream(sqlQuery, tupleTypeInfo);
		//TODO 进行map处理并赋值到kafka当中
//		retractStream.map(new MapFunction<Tuple2<Boolean,OutPutVO>, String>() {
//			@Override
//			public String map(Tuple2<Boolean, OrignalLogVO> value) throws Exception {
//				OrignalLogVO orignalLogVO = value.f1;
//				StreamMidVO streamMidVO  = new StreamMidVO();
//				streamMidVO.setLogsInfo(gson.toJson(orignalLogVO));
//				streamMidVO.setResultGuid(UUID.randomUUID().toString());
//				streamMidVO.setRuleCode(ruleCode);
//				streamMidVO.setRelatedIps(orignalLogVO.getRelated_ips());
//				streamMidVO.setSrc_ips(orignalLogVO.getSrc_ips());
//				streamMidVO.setSrc_ports(orignalLogVO.getSrc_ports());
//				streamMidVO.setDstIps(orignalLogVO.getDst_ips());
//				streamMidVO.setDst_ports(orignalLogVO.getDst_ports());
//				streamMidVO.setTriggerTime(DateUtil.timeStampTransferDate(orignalLogVO.getTrigger_time()));
//				String json = gson.toJson(streamMidVO);
//				return json;
//			}
//		});
		//.addSink(new FlinkKafkaProducer010<String>(KAFKA_PRODUCER_URL, TOPIC_PRODUCER_NAME, new SimpleStringSchema()));
		//dataStream.writeAsText(file_path);
		retractStream.print();
		env.execute("AlarmAnalysisSource Job");
	}
	
}
