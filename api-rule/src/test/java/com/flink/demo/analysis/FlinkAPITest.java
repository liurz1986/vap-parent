package com.flink.demo.analysis;

import java.util.List;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import com.flink.demo.vo.POJOTestVO;
import com.flink.demo.vo.SwitchFlagVO;
import com.flink.demo.vo.SwitchFoldVO;
import com.flink.demo.vo.WaterMarkVO;
import com.flink.demo.vo.WordCountVO;
import com.flink.demo.vo.window.BoundedOutOfOrdernessGenerator;
import com.vrv.logVO.monior.IfEntry;
import com.vrv.logVO.monior.SwitchVo;

/**
 * @author wudi E-mail:wudi891012@163.com
 * @version 创建时间：2018年10月26日 下午1:53:23 类说明
 */
public class FlinkAPITest {

	public static void main(String[] args) throws Exception {
		// final StreamExecutionEnvironment env =
		// StreamExecutionEnvironment.getExecutionEnvironment();
		final LocalStreamEnvironment localEnvironment = StreamExecutionEnvironment.createLocalEnvironment();
		// tupleTest(localEnvironment);
		// pojoTest(env);
		// mapApiTest(env);
		// generateSequenceTest(env);
		// iterationMapTest(env);
		// localMapTest();
		// keySelector(localEnvironment);
		testSwitchPortsCount(localEnvironment);
		localEnvironment.execute("flink-api");
	}

	@SuppressWarnings("deprecation")
	public static void testSwitchPortsCount(StreamExecutionEnvironment env) {
		List<SwitchVo> switch1 = POJOTestVO.getSwitch();
		DataStream<SwitchVo> dataStream = env.fromCollection(switch1);
		DataStream<SwitchFlagVO> fold = dataStream.flatMap(new FlatMapFunction<SwitchVo, SwitchFlagVO>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void flatMap(SwitchVo switchVO, Collector<SwitchFlagVO> out) throws Exception {
				IfEntry[] ifEntryList = switchVO.getIfEntryList();
				for (int i = 0; i < ifEntryList.length; i++) {
					SwitchFlagVO switchFlagVO = new SwitchFlagVO();
					switchFlagVO.setSwitchId(switchVO.getAssetGuid());
					switchFlagVO.setException(false);
					switchFlagVO.setPortCount(ifEntryList.length);
					switchFlagVO.setPortId(ifEntryList[i].getIfIndex());
					switchFlagVO.setHappenTime(switchVO.getTriggerTime());
					switchFlagVO.setSpeedValue(Integer.valueOf(ifEntryList[i].getIfMtu()));
					switchFlagVO.setAccumulateCount(0);
					out.collect(switchFlagVO);
				}
			}
		});

		fold.keyBy("switchId").reduce(new ReduceFunction<SwitchFlagVO>() {
			/**
			 * accumulator作为上一个返回的数据
			 * SwitchFlagVO是新增的数据
			 * value
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public SwitchFlagVO reduce(SwitchFlagVO accumulator, SwitchFlagVO value) throws Exception {
				Integer accumulateCount = accumulator.getAccumulateCount();
				if (accumulateCount == 0) {
					SwitchFlagVO switchFlagVO = new SwitchFlagVO();
					switchFlagVO.setPortId(accumulator.getPortId());
					switchFlagVO.setSwitchId(accumulator.getSwitchId());
					switchFlagVO.setSpeedValue(switchFlagVO.getSpeedValue());
					switchFlagVO.setAccumulateCount(1);
					if (value.getSpeedValue() > 1000) {
						switchFlagVO.setExceptionCount(1);
						switchFlagVO.setException(true);
						switchFlagVO.setAccumulateCount(accumulator.getAccumulateCount() + 1);
						switchFlagVO.setExceptionInfo(switchFlagVO.getPortId() + "-" + value.getSpeedValue());
					}
					return switchFlagVO;
				} else {
					accumulator.setAccumulateCount(accumulator.getAccumulateCount() + 1);
					if (value.getSpeedValue() > 1000) {
						accumulator.setExceptionCount(accumulator.getExceptionCount() + 1);
						accumulator.setException(true);
						accumulator.setExceptionInfo(
						accumulator.getExceptionInfo() + "|" + value.getPortId() + "-" + value.getSpeedValue());
					}
					return accumulator;
				}
			}
		}).writeAsText("D:\\tmp\\sss");

	}

	private static void keySelector(StreamExecutionEnvironment env) {
		List<WordCountVO> list = POJOTestVO.getPojoTest();
		DataStreamSource<WordCountVO> dataStreamSource = env.fromCollection(list);

		KeyedStream<WordCountVO, String> keyBy = dataStreamSource.keyBy(new KeySelector<WordCountVO, String>() {
			@Override
			public String getKey(WordCountVO value) throws Exception {
				return value.getWord();
			}
		});
		keyBy.print();
	}

	private static void tupleTest(StreamExecutionEnvironment env) {
		List<Tuple3<Integer, String, Integer>> list = POJOTestVO.inputTest();
		DataStreamSource<Tuple3<Integer, String, Integer>> dataSource = env.fromCollection(list);
		DataStream<Tuple3<Integer, String, Integer>> sum = dataSource.keyBy(1).minBy(0);

		// sum.writeAsText("D:\\tmp\\file\\number");
		sum.print();
	}

	/**
	 * pojo实体，合并hello pojo实体和world pojo实体
	 * 
	 * @param env
	 */
	private static void pojoTest(StreamExecutionEnvironment env) {
		DataStreamSource<WordCountVO> streamSource = env.fromElements(new WordCountVO("hello", 1),
				new WordCountVO("world", 2));
		// TODO key by 只能是按照某一个key进行分组
		DataStream<WordCountVO> reduce = streamSource.countWindowAll(2).reduce(new ReduceFunction<WordCountVO>() {
			@Override
			public WordCountVO reduce(WordCountVO value1, WordCountVO value2) throws Exception {
				WordCountVO wordCountVO = new WordCountVO();
				wordCountVO.setCount(value1.getCount() + value2.getCount());
				wordCountVO.setWord(value1.getWord() + value2.getWord());
				return wordCountVO;
			}
		});
		reduce.writeAsText("D:\\tmp\\file\\reduce");
	}

	/**
	 * map API
	 * 
	 * @param text
	 */
	private static void mapApiTest(StreamExecutionEnvironment env) {

		DataStream<Tuple2<String, Integer>> wordCounts = env.fromElements(new Tuple2<String, Integer>("hello", 1),
				new Tuple2<String, Integer>("world", 2));
		DataStream<Integer> map = wordCounts.map(new MapFunction<Tuple2<String, Integer>, Integer>() {
			@Override
			public Integer map(Tuple2<String, Integer> value) throws Exception {
				return value.f1 * 2;
			}
		});
		map.writeAsText("D:\\tmp\\file\\hello-world");
	}

	/**
	 * 通过flink完成求和操作
	 * 
	 * @param env
	 */
	private static void generateSequenceTest(StreamExecutionEnvironment env) {
		DataStreamSource<Long> streamSource = env.generateSequence(1, 10);
		DataStream<Tuple2<String, Long>> sum = streamSource.flatMap(new FlatSum()).keyBy(0).sum(1);
		sum.writeAsText("D:\\tmp\\file\\sumNum");
	}

	public static class FlatSum implements FlatMapFunction<Long, Tuple2<String, Long>> {
		@Override
		public void flatMap(Long number, Collector<Tuple2<String, Long>> out) throws Exception {
			out.collect(new Tuple2<String, Long>("number", number));
		}
	}

	public static void iterationMapTest(StreamExecutionEnvironment env) {
		DataStream<Long> someIntegers = env.generateSequence(0, 10);
		IterativeStream<Long> minusOne = someIntegers.iterate();
		DataStream<Long> filter = minusOne.map(i -> i - 1).filter(i -> i > 0);
		minusOne.closeWith(filter);
		filter.print();
	}

	public static void localMapTest(LocalStreamEnvironment localEnvironment) {
		DataStreamSource<Long> dataStreamSource = localEnvironment.generateSequence(1, 1000);
		DataStream<Long> filter = dataStreamSource.map(i -> i - 1);
		filter.print();
	}

	public static void warterMarkTest(LocalStreamEnvironment localEnvironment) {
		DataStreamSource<WaterMarkVO> fromElements = localEnvironment.fromElements(new WaterMarkVO(1000, "wudi", 29));
		fromElements.keyBy(new KeySelector<WaterMarkVO, String>() {
			@Override
			public String getKey(WaterMarkVO value) throws Exception {
				return value.getName();
			}

		}).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator());
	}

}
