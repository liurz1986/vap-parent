package com.flink.demo.sqltest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.flink.api.common.functions.MapFunction;
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
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.util.JdbcConnectionUtil;
import com.vrv.rule.vo.FieldTypeEnum;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月5日 下午4:11:53
* 类说明 采用jdbc的方式获得对应的方式
*/
public class UserProcessActionSourceJdbc implements StreamTableSource<Row>,DefinedProctimeAttribute,Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static final String KAFKA_PRODUCER_URL  = "192.168.120.104:9092";
	public static final String TOPIC_PRODUCER_NAME = "flink-wiki-demo";
	public static final String KAFKA_CONSUMER_URL = "192.168.120.104:9092";
	public static final String TOPIC_CONSUMER_NAME = "flink-kafka";
	public static final String KAFKA_GROUP_ID = "test";
	public static final String event_table_sql = "select id from event_table where name= ";
	public static final String event_column_name_sql = "select name,srcIp,dstIp,relateIp from event_column where EventTable = ";
	public static final String event_column_type_sql = "select type from event_column where EventTable = ";
	
	
	private String tableName;   //原始日志表名
	private String[] field = null;
	public List<Map<String,Object>> fieldsList = new ArrayList<>();
	private TypeInformation[] typeInformation= null;
	
	
	public UserProcessActionSourceJdbc(){}
	
	
	public UserProcessActionSourceJdbc(String tableName){
		this.tableName = tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	@Override
	public String explainSource() {
		return "UserActions";
	}

	@Override
	public TypeInformation<Row> getReturnType() {
		TypeInformation<Row> typeInformationTypes = getTypeInformationTypes();
		return typeInformationTypes;
	}

	@Override
	public TableSchema getTableSchema() {
		String[] field = getField();
		TypeInformation[] types = getTypeInformation();
		return new TableSchema(
				field,types);
	}

	@Override
	public String getProctimeAttribute() {
		return "triggerTime"; //machine local time
	}

	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env){
		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", KAFKA_CONSUMER_URL);
		properties.setProperty("group.id", KAFKA_GROUP_ID);
		TypeInformation<Row> row = getTypeInformationTypes();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(TOPIC_CONSUMER_NAME,new JsonRowDeserializationSchema(row) , properties));
		
		return addSource;
	
	}
	
	
	/**
	 * 获得TypeInformationTypes对应的信息(pre_dynamic_tables)
	 * @return
	 */
	private TypeInformation<Row> getTypeInformationTypes() {
		String[] field = getField();
		TypeInformation[] types = getTypeInformation();
		TypeInformation<Row> row = Types.ROW(field, types);
		return row;
	}
	
	/**
	 * 获得对应的匹配类型
	 * @return
	 */
	 private TypeInformation[] getTypeInformation(){
		 if(typeInformation==null){
			 List<TypeInformation> list = new ArrayList<>();
			 Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(event_table_sql+"'"+tableName+"'");
			 String id = map.get("id").toString(); //event_table的id
			 List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(event_column_type_sql+"'"+id+"'");
			 for (Map<String, Object> map2 : querySqlForList) {
				 String type = map2.get("type").toString();
				 TypeInformation flinkType = FieldTypeEnum.getFlinkType(type);
				 list.add(flinkType);
			 }
			 TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
			 typeInformation = types;
		 }
		return typeInformation;
	 }
	
	
	/**
	 * 获得原始日志字段
	 * @return
	 */
	private  String[] getField(){
		if(field==null){		
			List<String> list  = new ArrayList<>();
			Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(event_table_sql+"'"+tableName+"'");
			String id = map.get("id").toString(); //event_table的id
			List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(event_column_name_sql+"'"+id+"'");
			fieldsList.addAll(querySqlForList);
			for (Map<String, Object> map2 : querySqlForList) {
				String name = map2.get("name").toString();
				list.add(name);
			}
			String[] strs1=list.toArray(new String[list.size()]);
			field = strs1;
		}
	    return field;
	}
	
	
	public static void main(String[] args) throws Exception {
		String tableName = "probe_netflow";
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型(处理时间)
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		
		UserProcessActionSourceJdbc userProcessActionSourceJdbc2 = new UserProcessActionSourceJdbc(tableName);
		Table table = sTableEnv.fromTableSource(userProcessActionSourceJdbc2);
		sTableEnv.registerTable(tableName, table);
		
		Table sqlQuery = sTableEnv.sqlQuery("select * from UserActions");
		Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(event_table_sql+"'"+tableName+"'");
		String id = map.get("id").toString(); //event_table的id
		List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(event_column_name_sql+"'"+id+"'");
		DataStream<Tuple2<Boolean,Row>> retractStream = sTableEnv.toRetractStream(sqlQuery, Row.class);
		retractStream.map(new MapFunction<Tuple2<Boolean,Row>,String>() {
			@Override
			public String map(Tuple2<Boolean, Row> value) throws Exception {
				Map<String,Object> map  = new HashMap<>();
				Row row = value.f1;
				int arity = row.getArity();
				String[] field2 = userProcessActionSourceJdbc2.getField();
				String src_Ip = null;
				String dst_Ip = null;
				String relate_ip  = null;
				String logInfo = null;
				for (int i = 0; i < arity; i++) {
					String columnName = field2[i];
					Boolean result1=(Boolean)querySqlForList.get(i).get("srcIp");
					if(result1){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							src_Ip = fieldValue.toString();
							map.put(columnName, src_Ip);
							continue;
						}
					}
					Boolean result2=(Boolean)querySqlForList.get(i).get("dstIp");
					if(result2){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							dst_Ip = fieldValue.toString();
							map.put(columnName, dst_Ip);
							continue;
						}
					}
					
					Boolean result3=(Boolean)querySqlForList.get(i).get("relateIp");
					if(result3){
						Object fieldValue = row.getField(i);
						if(fieldValue!=null){
							relate_ip = fieldValue.toString();
							map.put(columnName, relate_ip);
							continue;
						}
					}
					map.put(columnName, row.getField(i));
				}
				logInfo = gson.toJson(map);
				StreamMidVO streamMidVO  = new StreamMidVO(UUID.randomUUID().toString(), 
						"rule_code", new Date(), src_Ip, dst_Ip,relate_ip, logInfo,null, null);
				String json = gson.toJson(streamMidVO);
				System.out.println("json:"+json);
				return json;
			}
		});
		retractStream.print();
		env.execute();
	}
	
	
}
