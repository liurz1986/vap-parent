package com.vrv.rule.source;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.sources.DefinedProctimeAttribute;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.LogDesc;
import com.vrv.rule.util.YmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月27日 下午3:54:11 
* 类说明    统一的资源类
 * @param <T>
*/
public class DataStreamSource<T> implements StreamTableSource<T>,DefinedProctimeAttribute,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(DataStreamSource.class);

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	protected Class<T> clazz;
	protected String topicName;
	protected String timeField;
	
	
	
	public DataStreamSource(Class<T> clazz,String topicName){
		this.clazz = clazz;
		this.topicName = topicName;
	}
	
	public DataStreamSource(Class<T> clazz){
		this.clazz = clazz;
	}
	
	
	
	
	
	/**
	 * 获得kafka的group_Id
	 * @return
	 */
	public  String getKafkaGroupId(){
		return getOrignalLogName();
		
	}

    /**
     * 获得原始日志名称
     * @return
     */
	private String getOrignalLogName() {
		if(topicName==null){
			LogDesc logDesc = clazz.getAnnotation(LogDesc.class);
			String topicName = logDesc.topicName();
			return topicName;			
		}else {
			return topicName;
		}
	}
	
	
	/**
	 * 获得source消费的kafka的topic名称
	 * @return
	 */
	public String getKafkaConsumerName(){
		return getOrignalLogName();
	}
	
	/**
	 * 流数据源资源
	 */
	@Override
	public String explainSource() {
		return getOrignalLogName();
	}

	/**
	 *返回数据源的类型信息
	 */
	@Override
	public TypeInformation<T> getReturnType() {
		TypeInformation<T> typeInformation = TypeInformation.of(clazz);
		return typeInformation;
	}

	/**
	 * 返回TableSchema信息
	 */
	@Override
	public TableSchema getTableSchema() {
		TableSchema tableSchema = TableSchema.fromTypeInfo(getReturnType());
		return tableSchema;
	}

	@Override
	public String getProctimeAttribute() {
		return "triggerTime"; //触发时间（统一标注触发时间）
	}

	@Override
	public DataStream<T> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		properties.setProperty("group.id", getKafkaGroupId()+UUID.randomUUID().toString());
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //TODO 从头开始消费数据
		DataStream<String> addSource = env.addSource(new FlinkKafkaConsumer<>(getKafkaConsumerName(),
				new SimpleStringSchema(), properties));
		DataStream<T> returns = addSource.map(new MyMapFunction<String, T>() {
			private static final long serialVersionUID = 1L;
			@Override
			public T map(String value) throws Exception {
				try {
					T t= gson.fromJson(value, clazz);					
					return t;					
				}catch(Exception e){
					logger.error("该条日志"+value+"出现问题，请处理!");
					throw new RuntimeException(e);
				}
			}
			@Override
			public TypeInformation<T> getProducedType() {
				TypeInformation<T> typeInformation = TypeInformation.of(clazz);
				return typeInformation;
			}
		});
	   return returns;
	}

	

}
