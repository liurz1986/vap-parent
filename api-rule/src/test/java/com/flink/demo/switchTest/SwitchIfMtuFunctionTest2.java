package com.flink.demo.switchTest;

import java.util.List;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import com.flink.demo.udf.SwitchFlatMapFunction;
import com.flink.demo.vo.POJOTestVO;
import com.flink.demo.vo.SwitchFlagVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.logVO.monior.SwitchVo;
import com.vrv.rule.ruleInfo.udf.AccountArrayFunction;
import com.vrv.rule.ruleInfo.udf.SumArrayFunction;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月11日 上午11:53:39 
* 类说明 
*"select assetGuid,inDate,icmpPing,collectorIp,"
				+ "instanceId,dataPickerPlugin,event_Table_Name,runningTime,avgBusy1,h3cEntityExtMemUsage,"
				+ "ifNumber,ifIndex,ifDescr,ifType,ifMtu,ifSpeed,ifPhysAddress,ifAdminStatus,ifOperStatus,"
				+ "ifLastChange,ifInOctets,ifInUcastPkts,ifInDiscards,triggerTime,count(ifMtu) OVER (PARTITION BY collectorIp ORDER BY "
				+ "triggerTime RANGE BETWEEN INTERVAL '2' SECOND preceding AND CURRENT ROW) AS ifMtuMax from SwitchVo"
*/
public class SwitchIfMtuFunctionTest2 {
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		
		List<SwitchVo> switch1 = POJOTestVO.getSwitch();
		DataStream<SwitchVo> dataStream = env.fromCollection(switch1);
		//String sql = "select * from SwitchVo where ifEntryList[1].ifMtu>1000"; //某一个端口的值大于阈值，产生告警
		//String sql ="select * from SwitchVo where sumArrayFunction(ifEntryList,'ifMtu')>1000"; //所有端口数据流量总和超过阈值，产生告警
	//String sql = "select * from SwitchVo where accountArrayFunction(ifEntryList,'ifMtu','>',1200)>5"; //有5个端口的数据流超过阈值，则发生报警
		sTableEnv.registerFunction("sumArrayFunction", new SumArrayFunction());
		sTableEnv.registerFunction("accountArrayFunction", new AccountArrayFunction());
		sTableEnv.registerFunction("switchFlagMapFunction", new SwitchFlatMapFunction());
		sTableEnv.registerDataStream("SwitchVo", dataStream);
	    String sql = "select switchId,portCount,portId,exception,speedValue,happenTime from SwitchVo,LATERAL TABLE(switchFlagMapFunction(assetGuid,ifEntryList,triggerTime))";
		Table sqlQuery = sTableEnv.sqlQuery(sql);  //"select * from windows_log where severity='ERROR'"
		//TODO 采用结构化数据将数据输入到kafka当中
		DataStream<Tuple2<Boolean,SwitchFlagVO>> retractStream = sTableEnv.toRetractStream(sqlQuery, SwitchFlagVO.class);
		
		DataStream<SwitchFlagVO> switchFlagVODataStream = retractStream.map(new MapFunction<Tuple2<Boolean,SwitchFlagVO>, SwitchFlagVO>() {
			@Override
			public SwitchFlagVO map(Tuple2<Boolean, SwitchFlagVO> value) throws Exception {
				SwitchFlagVO switchFlagVO = value.f1;
				return switchFlagVO;
			}
		});
		sTableEnv.registerDataStream("SwitchFlagVO", switchFlagVODataStream);
		
		
		retractStream.print();
		env.execute("jobName"); //"Honey Pot Intrusion Job"
	}
	
	


}
