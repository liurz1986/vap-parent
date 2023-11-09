package com.vrv.rule.source;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.vrv.logVO.LogDesc;
import com.vrv.rule.util.YmlUtil;
import com.vrv.rule.vo.FieldTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月27日 下午3:54:11 
* 类说明    统一的资源类
 * @param <T,V>  T:表示输入流  V：表示输出流
*/
public class DataStreamNewSource<T,V> implements StreamTableSource<Row>,DefinedProctimeAttribute,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(DataStreamNewSource.class);

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	
	protected Class<T> clazz;
	protected Class<V> clavv;
	
	
	
	
	/**
	 * clazz输入VO,clavv输出结果
	 * @param clazz
	 * @param clavv
	 */
	public DataStreamNewSource(Class<T> clazz,Class<V> clavv){
		this.clazz = clazz;
		this.clavv = clavv;
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
	public TypeInformation<Row> getReturnType() {
		String[] fields = getFieldNames(clazz);
		TypeInformation[] typeInformations = getFieldType(clazz);
		TypeInformation<Row> row = Types.ROW(fields, typeInformations);
		return row;
	}
	
	
	/**
	 * 源数据
	 * @return
	 */
	public TypeInformation<Row> getTableType() {
		String[] fields = getFieldNames(clazz);
		TypeInformation[] typeInformations = getFieldType(clazz);
		TypeInformation<Row> row = Types.ROW(fields, typeInformations);
		return row;
	}
	

	/**
	 * 字段类型
	 * @return
	 */
   private TypeInformation[] getFieldType(Class<?> clazz){
	   List<TypeInformation> list = new ArrayList<>();
	   Field[] fields = clazz.getDeclaredFields();
	   for (Field field : fields) {
		   field.setAccessible(true); 
		   Type genericType = field.getGenericType();
   		   String typeName = genericType.getTypeName();
   		   TypeInformation typeInformation = FieldTypeEnum.getFlinkType(typeName);
   		   list.add(typeInformation);
	}
	   TypeInformation[] types = list.toArray(new TypeInformation[list.size()]);
	   return types;
   }
	
   /**
    * 字段名称
    * @return
    */
	private String[] getFieldNames(Class<?> clazz) {
		List<String> list = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true); 
			String name = field.getName();
			list.add(name);
		}
		String[] strs1=list.toArray(new String[list.size()]);
		return strs1;
	}

	/**
	 * 返回TableSchema信息
	 */
	@Override
	public TableSchema getTableSchema() {
		TableSchema tableSchema = TableSchema.fromTypeInfo(getTableType());
		return tableSchema;
	}

	@Override
	public String getProctimeAttribute() {
		return "triggerTime"; //触发时间（统一标注触发时间）
	}

	@Override
	public DataStream<Row> getDataStream(StreamExecutionEnvironment env) {
		Properties properties = new Properties();
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
		properties.setProperty("bootstrap.servers", url+":"+port);
		properties.setProperty("group.id", getKafkaGroupId());
		TypeInformation<Row> typeInformation = getTableType();
		DataStream<Row> addSource = env.addSource(new FlinkKafkaConsumer<>(getKafkaConsumerName(),
				new JsonRowDeserializationSchema(typeInformation), properties));
//		DataStream<T> returns = addSource.map(new MyMapFunction<String, T>() {
//			private static final long serialVersionUID = 1L;
//			@Override
//			public T map(String value) throws Exception {
//				T t = gson.fromJson(value, clazz);
//				return t;					
//			}
//			@Override
//			public TypeInformation<T> getProducedType() {
//				TypeInformation<T> typeInformation = TypeInformation.of(clazz);
//				return typeInformation;
//			}
//		});
	   return addSource;
	}

	

}
