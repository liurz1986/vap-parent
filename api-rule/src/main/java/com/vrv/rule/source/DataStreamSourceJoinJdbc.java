package com.vrv.rule.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.util.JdbcConnectionUtil;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.FieldTypeEnum;

/**
* @author wudi E-mail:wudi891012@163.com
* @version 创建时间：2018年11月5日 下午4:11:53
* 类说明 采用jdbc的方式获得Join连接查询
*/
public class DataStreamSourceJoinJdbc implements StreamTableSource<Row>,DefinedProctimeAttribute,Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	private String tableName;   //原始日志表名
	private String topicName; //
	private String[] field = null;
	public List<Map<String,Object>> fieldsList = new ArrayList<>();
	private TypeInformation[] typeInformation= null;
	
	
	public DataStreamSourceJoinJdbc(){}
	
	
	public DataStreamSourceJoinJdbc(String tableName,String topicName){
		this.tableName = tableName;
		this.topicName = topicName;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	
	public String getTopicName() {
		return topicName;
	}


	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}


	@Override
	public String explainSource() {
		return "JDBCSource";
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
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		String kafkaTopicName = topicName;
		properties.setProperty("group.id", kafkaTopicName+UUID.randomUUID().toString());
		TypeInformation<Row> row = getTypeInformationTypes();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(kafkaTopicName,new JsonRowDeserializationSchema(row) , properties));
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
			 Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(JdbcConnectionUtil.event_table_sql+"'"+tableName+"'");
			 String id = map.get("id").toString(); //event_table的id
			 List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(JdbcConnectionUtil.event_column_type_sql+"'"+id+"'");
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
	public  String[] getField(){
		if(field==null){		
			List<String> list  = new ArrayList<>();
			Map<String, Object> map = JdbcConnectionUtil.querySqlForMap(JdbcConnectionUtil.event_table_sql+"'"+tableName+"'");
			String id = map.get("id").toString(); //event_table的id
			List<Map<String,Object>> querySqlForList = JdbcConnectionUtil.querySqlForList(JdbcConnectionUtil.event_column_field_sql+"'"+id+"'");
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
	
}
