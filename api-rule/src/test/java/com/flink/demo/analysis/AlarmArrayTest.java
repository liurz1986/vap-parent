package com.flink.demo.analysis;

import java.util.Arrays;
import java.util.List;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class AlarmArrayTest {

	public static void main(String[] args) throws Exception {
		LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(localEnvironment,fsSettings);
		 List<Row> rows = Arrays.asList(
	                Row.of(1, new Row[]{Row.of(12, "sd"), Row.of(15, "sd")}),
	                Row.of(2, new Row[]{Row.of(13, "sd"), Row.of(16, "sd")}),
	                Row.of(3, new Row[]{Row.of(14, "sd"), Row.of(17, "sd")})
	        );
		 
		 TypeInformation<?>[] types = new TypeInformation[]{Types.INT(), Types.OBJECT_ARRAY(Types.ROW(Types.INT(),Types.STRING()))};
		 String[] typeNames = new String[]{"a", "b"};
		 DataStream<Row> source = localEnvironment
	                .fromCollection(rows)
	                .returns(new RowTypeInfo(types, typeNames));
		 tableEnvironment.registerDataStream("source", source);
		 Table a = tableEnvironment.sqlQuery("select a,b[1] from source");
         tableEnvironment.toAppendStream(a, Row.class).writeAsText("D:\\tmp\\printer");
         localEnvironment.execute();
	}
	
	
}
