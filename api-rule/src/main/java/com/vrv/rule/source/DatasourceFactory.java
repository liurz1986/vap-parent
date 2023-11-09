package com.vrv.rule.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.sources.StreamTableSource;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.types.Row;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月17日 下午3:59:35 
* 类说明      数据源Factory
*/
public class DatasourceFactory {

	
	public static <T> TableSource<T> create(Class<T> class1) {
		StreamTableSource<T> streamTableSource = new DataStreamSource<T>(class1);
		return streamTableSource;
	}
	
	
	
	
	
	
	
	public static <T> TableSource<T> create(Class<T> class1,String topicName) {
		StreamTableSource<T> streamTableSource = new DataStreamSource<T>(class1,topicName);
		return streamTableSource;
	}

	/**
	 * 事件类型
	 * @param class1
	 * @param type
	 * @return
	 */
	public static <T,V> TableSource<Row> create(Class<T> clazz,Class<V> clavv) {
		StreamTableSource<Row> streamTableSource = new DataStreamNewSource<T, V>(clazz, clavv);
		return streamTableSource;
	} 
	
	
}
