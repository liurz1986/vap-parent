package com.flink.demo.analysis;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.flink.demo.vo.Person;


/**
 * flink启动测试
 * @author Administrator
 *
 */
public class FlinkStartFunctionTest {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		//env.setRuntimeMode(RuntimeExecutionMode.BATCH);
		DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Pebbles", 2));
		
		DataStream<Person> adults = flintstones.filter(new FilterFunction<Person>() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });
		adults.print();
        env.execute();
		
		
	}
	
}
