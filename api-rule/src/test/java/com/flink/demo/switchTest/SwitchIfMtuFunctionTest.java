package com.flink.demo.switchTest;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.sources.TableSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.rule.ruleInfo.udf.IpResourceFunction;
import com.vrv.rule.ruleInfo.udf.PortResourceFunction;
import com.vrv.rule.ruleInfo.udf.RegularExpressionFunction;
import com.vrv.rule.ruleInfo.udf.TimeResourceFunction;
import com.vrv.rule.source.DatasourceFactory;
import com.vrv.rule.util.YmlUtil;

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
public class SwitchIfMtuFunctionTest {
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static <T> void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime); //设置对应的时间类型
		EnvironmentSettings fsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
		StreamTableEnvironment sTableEnv = StreamTableEnvironment.create(env,fsSettings);
		
		String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
		String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();		
		String rule_code = "33"; //规则编码 args[0]
		System.out.println("rule_code:"+rule_code);
		String jobName = "switch job"; //任务名称args[1]
		System.out.println("jobName:"+jobName);
		String orignalLogPath = "com.vrv.logVO.monior.SwitchVo";//args[2]; //原始日志路径
		System.out.println("orignalLogPath:"+orignalLogPath);
		//String sql = "select * from SwitchVo where ifEntryList[1].ifMtu>1000"; //某一个端口的值大于阈值，产生告警
		//String sql ="select * from SwitchVo where sumArrayFunction(ifEntryList,'ifMtu')>1000"; //所有端口数据流量总和超过阈值，产生告警
		//String sql = "select SwitchVo.collectorIp as collectorIp from SwitchVo where (ipResourceFunction(SwitchVo.collectorIp,'contain','127.0.0.1,192.168.102.24/10')=1) "; //有5个端口的数据流超过阈值，则发生报警
		String sql = "select SwitchVo.collectorIp as collectorIp from SwitchVo where (regularExpressionFunction(SwitchVo.collectorIp,'((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))')=1)";
		
		System.out.println("sql:"+sql);
		String tableName = "SwitchVo";//args[4];
		System.out.println("tableName:"+tableName);
		Class<?> forName = Class.forName(orignalLogPath);
		Class<T> clazz= (Class<T>)forName;
//		sTableEnv.registerFunction("sumArrayFunction", new SumArrayFunction());
//		sTableEnv.registerFunction("accountArrayFunction", new AccountArrayFunction());
		sTableEnv.registerFunction("ipResourceFunction", new IpResourceFunction());
		sTableEnv.registerFunction("timeResourceFunction", new TimeResourceFunction());
		sTableEnv.registerFunction("portResourceFunction", new PortResourceFunction());
		sTableEnv.registerFunction("regularExpressionFunction", new RegularExpressionFunction());
		TableSource<T> tableSource = DatasourceFactory.create(clazz);
		Table table = sTableEnv.fromTableSource(tableSource);
		sTableEnv.registerTable(tableName, table);
		
		
		Table sqlQuery = sTableEnv.sqlQuery(sql);  //"select * from windows_log where severity='ERROR'"
		//TODO 采用结构化数据将数据输入到kafka当中
		//DataStream<Tuple2<Boolean,T>> retractStream = sTableEnv.toRetractStream(sqlQuery, clazz);
		DataStream<Tuple2<Boolean,String>> retractStream = sTableEnv.toRetractStream(sqlQuery, String.class);
		retractStream.print();
		env.execute(jobName); //"Honey Pot Intrusion Job"
	}
	
	


}
