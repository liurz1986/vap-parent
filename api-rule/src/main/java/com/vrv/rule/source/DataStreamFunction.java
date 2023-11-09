package com.vrv.rule.source;

import java.io.Serializable;
import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.LogDesc;
import com.vrv.rule.util.YmlUtil;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年9月2日 下午6:45:47 
* 类说明    不采用自定义，而是采用普通方式
*/
public class DataStreamFunction<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    protected Class<T> clazz;
	 
	public DataStreamFunction(Class<T> clazz){
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
		LogDesc logDesc = clazz.getAnnotation(LogDesc.class);
		String topicName = logDesc.topicName();
		return topicName;
	}
	
	
	/**
	 * 获得source消费的kafka的topic名称
	 * @return
	 */
	public String getKafkaConsumerName(){
		return getOrignalLogName();
	}
	
	
	public DataStream<T> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		properties.setProperty("group.id", getKafkaGroupId());
		DataStream<String> addSource = env.addSource(new FlinkKafkaConsumer<>(getKafkaConsumerName(),
				new SimpleStringSchema(), properties));
		DataStream<T> returns = addSource.map(new MyMapFunction<String, T>() {
			private static final long serialVersionUID = 1L;
			@Override
			public T map(String value) throws Exception {
				T t = gson.fromJson(value, clazz);
				return t;					
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
