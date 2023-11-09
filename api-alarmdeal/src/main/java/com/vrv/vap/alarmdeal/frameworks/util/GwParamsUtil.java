package com.vrv.vap.alarmdeal.frameworks.util;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月27日 上午9:24:29 
* 类说明      国网传参设置
*/
public class GwParamsUtil {

	
	/**
	 * 事件总数	
	 * @param map
	 * @return
	 */
	public static List<AnalysisVO> getAnalysisCount(Map<String,Object> map){
		
			String areaCode = map.get("areaCode").toString();
			
			List<AnalysisVO> list = new ArrayList<>();
			AnalysisVO userCode = new AnalysisVO();
			AnalysisVO netCode = new AnalysisVO();
			
			
			userCode.setSrcAreaCode(areaCode);
			userCode.setEventtypelevel("/behaveor/userbehaveorMonitor");
			userCode.setRiskEventName("用户行为");
			userCode.setExtraField("用户行为");
			list.add(userCode);
			
			netCode.setSrcAreaCode(areaCode);
			netCode.setEventtypelevel("/behaveor/netbehavormonitor"); //基线不合规
			netCode.setRiskEventName("网络行为");
			netCode.setExtraField("网络行为");
			list.add(netCode);
			
			return list;
		}
		
		
		
		/**
		 * 获得本体对应趋势的List
		 * @param map
		 * @return
		 */
	public static List<AnalysisVO> getSelfAnalysisList(Map<String,Object> map){
			
			String stime = map.get("stime").toString();
			String etime = map.get("etime").toString();
			String areaCode = map.get("areaCode").toString();
			String flag = map.get("flag").toString();
			
			List<AnalysisVO> list = new ArrayList<>();
			AnalysisVO userCode = new AnalysisVO();
			AnalysisVO safeLine = new AnalysisVO();
			AnalysisVO userPrivildge = new AnalysisVO();
			
			userCode.setSrcAreaCode(areaCode);
			userCode.setEventtypelevel("/audit/self/weakMonitor/userCode"); //获取弱口令
			userCode.setStime(stime);
			userCode.setRiskEventName("弱口令");
			userCode.setEtime(etime);
			userCode.setFlag(flag);
			list.add(userCode);
			
			safeLine.setSrcAreaCode(areaCode);
			safeLine.setEventtypelevel("/audit/self/weakMonitor/safeLine"); //基线不合规
			safeLine.setStime(stime);
			safeLine.setRiskEventName("基线不合规");
			safeLine.setEtime(etime);
			safeLine.setFlag(flag);
			list.add(safeLine);
			
			userPrivildge.setSrcAreaCode(areaCode);
			userPrivildge.setEventtypelevel("/audit/self/weakMonitor/patchInstallation"); // 未安装重要
			userPrivildge.setStime(stime);
			userPrivildge.setRiskEventName("未安装重要补丁");
			userPrivildge.setEtime(etime);
			userPrivildge.setFlag(flag);
			list.add(userPrivildge);
			return list;
		}
		
		
		/**
		 * 获得行为对应趋势的List
		 * @param map
		 * @return
		 */
	public static List<AnalysisVO> getBehaveorAnalysisList(Map<String,Object> map){
			
			String stime = map.get("stime").toString();
			String etime = map.get("etime").toString();
			String areaCode = map.get("areaCode").toString();
			String flag = map.get("flag").toString();
			
			List<AnalysisVO> list = new ArrayList<>();
			AnalysisVO userCode = new AnalysisVO();
			AnalysisVO safeLine = new AnalysisVO();
		
			
			userCode.setSrcAreaCode(areaCode);
			userCode.setEventtypelevel("/audit/behaveor/userbehaveorMonitor"); //用户行为
			userCode.setStime(stime);
			userCode.setRiskEventName("用户行为");
			userCode.setEtime(etime);
			userCode.setFlag(flag);
			list.add(userCode);
			
			safeLine.setSrcAreaCode(areaCode);
			safeLine.setEventtypelevel("/audit/behaveor/netbehavormonitor"); //网络行为
			safeLine.setStime(stime);
			safeLine.setRiskEventName("网络行为");
			safeLine.setEtime(etime);
			safeLine.setFlag(flag);
			list.add(safeLine);
			
			
			return list;
		}
	
	
	/**
	 * 获得本体告警数实体
	 * @param map
	 * @return
	 */
	public static List<AnalysisVO> getSelfAlarmCount(Map<String,Object> map){

		String areaCode = map.get("areaCode").toString();
		
		List<AnalysisVO> list = new ArrayList<>();
		AnalysisVO userCode = new AnalysisVO(); //用户密码
		AnalysisVO safeLine = new AnalysisVO(); //安全基线
		AnalysisVO virusUpdate = new AnalysisVO(); //病毒更新
		AnalysisVO openPort = new AnalysisVO(); //开放端口
		AnalysisVO createUser = new AnalysisVO(); //创建用户
		AnalysisVO userPriviledge = new AnalysisVO(); //用户权限
		AnalysisVO registerAlarm = new AnalysisVO();//注册用户
	
		
		userCode.setSrcAreaCode(areaCode);
		userCode.setEventtypelevel("/audit/self/weakMonitor/userCode"); //用户密码
		userCode.setRiskEventName("弱口令设备");
		list.add(userCode);
		
		safeLine.setSrcAreaCode(areaCode);
		safeLine.setEventtypelevel("/audit/self/weakMonitor/safeLine"); //安全基线
		safeLine.setRiskEventName("基线不合规");
		list.add(safeLine);
		
		
		virusUpdate.setSrcAreaCode(areaCode);
		virusUpdate.setEventtypelevel("/audit/self/weakMonitor/antivirusSoft"); //杀毒软件
		virusUpdate.setRiskEventName("病毒库未更新");
		list.add(virusUpdate);
		
		
		openPort.setSrcAreaCode(areaCode);
		openPort.setEventtypelevel("/audit/self/weakMonitor/openPort"); //开放端口
		openPort.setRiskEventName("高危开放端口");
		list.add(openPort);
		
		
		createUser.setSrcAreaCode(areaCode);
		createUser.setEventtypelevel("/audit/self/weakMonitor/userPrivildge"); //创建用户
		createUser.setRiskEventName("创建用户");
		list.add(createUser);
		
		
		userPriviledge.setSrcAreaCode(areaCode);
		userPriviledge.setEventtypelevel("/audit/self/weakMonitor/userPrivildge"); //用户权限
		userPriviledge.setRiskEventName("用户权限变更");
		list.add(userPriviledge);
		
		
		registerAlarm.setSrcAreaCode(areaCode);
		registerAlarm.setEventtypelevel("/audit/self/weakMonitor/registertableabnormal"); //注册表异常
		registerAlarm.setRiskEventName("注册表告警事件");
		list.add(registerAlarm);
		
		return list;
	
	}
	
	
	/**
	 * 获得一天所有的hours
	 * @return
	 */
	public static List<String> getAllElement(int j,int k){
		List<String> list = new ArrayList<>();
		for (int i = j; i < k; i++) {
			if(i<10) {
				list.add("0"+i);
			}else {
				list.add(String.valueOf(i));
			}
		}
		return list;
	}
	
	
	
	public static List<AnalysisVO> getAccessAlarm(Map<String,Object> map){
		List<AnalysisVO> list = new ArrayList<>();
		String stime = map.get("stime").toString();
		String etime = map.get("etime").toString();
		String areaCode = map.get("areaCode").toString();
		AnalysisVO deviceEvent = new AnalysisVO();
		AnalysisVO terminalEvent = new AnalysisVO();
		AnalysisVO visitEvent = new AnalysisVO();
		AnalysisVO netWorkEvent = new AnalysisVO();
		AnalysisVO systemEvent = new AnalysisVO();
		
		deviceEvent.setEventtypelevel("/access/deviceTag");
		deviceEvent.setExtraField("设备事件");
		deviceEvent.setStime(stime);
		deviceEvent.setEtime(etime);
		deviceEvent.setSrcAreaCode(areaCode);
		list.add(deviceEvent);
		
		terminalEvent.setEventtypelevel("/access/TerminalEvent");
		terminalEvent.setExtraField("终端事件");
		terminalEvent.setStime(stime);
		terminalEvent.setEtime(etime);
		terminalEvent.setSrcAreaCode(areaCode);
		list.add(terminalEvent);
		
		
		visitEvent.setEventtypelevel("/access/VisitorIncident");
		visitEvent.setExtraField("访客事件");
		visitEvent.setStime(stime);
		visitEvent.setEtime(etime);
		visitEvent.setSrcAreaCode(areaCode);
		list.add(visitEvent);
		
		
		netWorkEvent.setEventtypelevel("/access/NetworkEvents");
		netWorkEvent.setExtraField("网络事件");
		netWorkEvent.setStime(stime);
		netWorkEvent.setEtime(etime);
		netWorkEvent.setSrcAreaCode(areaCode);
		list.add(netWorkEvent);

		systemEvent.setEventtypelevel("/access/SystemEvent");
		systemEvent.setExtraField("系统事件");
		systemEvent.setStime(stime);
		systemEvent.setEtime(etime);
		systemEvent.setSrcAreaCode(areaCode);
		list.add(systemEvent);
		return list;

	}

	//根据工单id获取事件id,现在的工单id是事件id+戳拼接而成的
	public static String getEventIdByFlowId(String flowId) {
		//eventid_timestamp
		String[] array = flowId.split("$");
		return array[0];
	}


}
