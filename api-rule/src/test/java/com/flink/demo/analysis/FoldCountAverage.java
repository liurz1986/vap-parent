package com.flink.demo.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichAggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.source.KafkaDataStreamSource;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.vo.FieldInfoVO;

/**
 * 褶皱平均demo
 * @author wd-pc
 *
 */
public class FoldCountAverage extends RichAggregateFunction<Row,Row,Row> {

	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient ValueState<Row> sum;
	private Long count = 0L;
	
	private static final String url = "192.168.120.103";
	private static final  String port = "9092";
	private static final String checkPointDir = "file:///usr/local/vap/flink/medata";
	
	
	
	

	
	    @Override
	    public void open(Configuration config) {
		
	    	
	    	
	        @SuppressWarnings("deprecation")
			ValueStateDescriptor<Row> descriptor =new ValueStateDescriptor<>(
	                        "average", // the state name
	                        TypeInformation.of(Row.class) // type information
	                        ); // default value of the state, if nothing was set Row.of(0,0)
//	        StateTtlConfig ttlConfig = StateTtlConfig
//	    		    .newBuilder(Time.seconds(1000))
//	    		    .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
//	    		    .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
//	    		    .cleanupFullSnapshot()
//	    		    .build();
//	        
//	        descriptor.enableTimeToLive(ttlConfig);
	        sum = getRuntimeContext().getState(descriptor);
	    }

	    @SuppressWarnings("deprecation")
		public static void main(String[] args) throws Exception{
	    	StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
			//final LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
	    	env.setStateBackend(new MemoryStateBackend(checkPointDir,null));
	    	env.enableCheckpointing(5000);
	    	// 高级选项：
	    	// 设置模式为exactly-once （这是默认值）
	    	env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);;
	    	// 确保检查点之间有至少1000 ms的间隔【checkpoint最小间隔】
	    	env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
	    	//检查点必须在一分钟内完成，或者被丢弃【checkpoint的超时时间】
	    	env.getCheckpointConfig().setCheckpointTimeout(60000);
	    	env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
			
			List<FieldInfoVO> list = new ArrayList<>();
			FieldInfoVO field1 = new FieldInfoVO();
			field1.setFieldName("key");
			field1.setFieldType("bigint");
			list.add(field1);
			
			FieldInfoVO field2 = new FieldInfoVO();
			field2.setFieldName("sum");
			field2.setFieldType("bigint");
			list.add(field2);
			
			String groupId = "fold-group-id";
			DataStream<Row> source = KafkaDataStreamSource.getDataStreamSource(env,"foldTest",list,groupId);

			//DataStreamSource<Row> source = env.fromElements(Row.of(1,3L),Row.of(1,2L),Row.of(1,5L),Row.of(1,6L),Row.of(1,7L),Row.of(1,8L));
			Row outputRow = new Row(3);
			DataStream<Row> fold = source.keyBy(0).reduce(new ReduceFunction<Row>() {
				
				@Override
				public Row reduce(Row value1, Row value2) throws Exception {
					// TODO Auto-generated method stub
					return null;
				}
			});
//			fold.print();
			fold.map(new MapFunction<Row, String>() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String map(Row row) throws Exception {
					AverageVO averageVO = new AverageVO();
					averageVO.setCount((Long)row.getField(0));
					averageVO.setSum((Long)row.getField(1));
					averageVO.setAverage((double)row.getField(2));
					averageVO.setTime(DateUtil.format(new Date()));
					String json = gson.toJson(averageVO);
					return json;
				}
			}).addSink(new FlinkKafkaProducer<String>(url+":"+port, "fold_receive_topic", new SimpleStringSchema()));
			
			env.execute("fold test");
			
		}
	
	public static class AverageVO{
		private Long sum;
		private Long count;
		private double average;
		private String time;
		public Long getSum() {
			return sum;
		}
		public void setSum(Long sum) {
			this.sum = sum;
		}
		public Long getCount() {
			return count;
		}
		public void setCount(Long count) {
			this.count = count;
		}
		public double getAverage() {
			return average;
		}
		public void setAverage(double average) {
			this.average = average;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		
	}

	@Override
	public Row createAccumulator() {
		Row row = Row.of(0L,0L,0L);
		return row;
	}

	@Override
	public Row add(Row inputRow, Row accumulator) {
		Row row = null;
		try {
			row = sum.value();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(row==null) {
			row = Row.of(0L,0L);
			count++;
			row.setField(0, count);
		}else {
			count = (Long)row.getField(0);
			count++;
			row.setField(0, count);
		}
		
		Long initValue = (Long)row.getField(1);
		Object field1 = inputRow.getField(1);
		Long value = (Long)field1;
		initValue+=value;
		row.setField(1, initValue);
		
		//更新状态
		try {
			sum.update(row);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Long counts = (Long)row.getField(0);
		if(counts>0) {
			Long sum = (Long)row.getField(1);
			double average = sum/counts;
			accumulator.setField(0, counts);
			accumulator.setField(1, sum);
			accumulator.setField(2, average);
		}
	//	sum.clear();
		return accumulator;
	}

	@Override
	public Row getResult(Row accumulator) {
		return accumulator;
	}

	@Override
	public Row merge(Row a, Row b) {
		return null;
	}

}
