package com.vrv.vap.alarmdeal.business.asset.job;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.asset.model.StrategyStatistics;
import com.vrv.vap.alarmdeal.business.asset.repository.StrategyStatisticsRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.util.http.GetRegisterDeviceRequest;
import com.vrv.vap.alarmdeal.business.asset.util.http.NTDSRequest;
import com.vrv.vap.alarmdeal.business.asset.vo.AuditAssetVO;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.http.RequestTypeEnum;
import com.vrv.vap.jpa.req.HttpSyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.*;

/**
 * 安审核数据同步定时器
 *
 * 现场部署报错屏蔽 ---2022-05-19
 */
 @Configuration
 @EnableScheduling
public class SecurityAuditJob {
	 @Value("${ntds.asset.url}")
	private String assetAuditurl;
	@Value("${ntds.login.url}")
	private String loginUrl;
	@Value("${ntds.login.userName}")
	private String userName;
	@Value("${ntds.simpleSearch.url}")
	private String simpleSearchUrl;
	@Value("${ntds.tryCount:3}")
	private int tryCount;
	@Value("${ntds.getStaticsIndexNum.url}")
	private String getStaticsIndexNumUrl;
	@Autowired
	private StrategyStatisticsRepository strategyStatisticsRepository;
	@Autowired
	private HttpSyncRequest httpSyncRequest;
	private Logger logger = LoggerFactory.getLogger(SecurityAuditJob.class);
	@Autowired
	private AssetService assetService;

	@Autowired
	private TbConfService tbConfService;
	private Gson gson = new GsonBuilder().create();
	//key是功能fucid value是策略名称
	private Map<String, String> strategyMap = new HashMap() {
		{
			put("20", "硬件资源管理策略");
			put("21", "进程及软件管理策略");
			put("22", "主机安全策略");
			put("23", "补丁分发策略");
			put("24", "软件分发策略");
			put("25", "接入认证策略");
			put("26", "违规外联监控策略");
			put("27", "行为管理及审计策略");
			put("28", "涉密检查策略");
			put("29", "可移动存储管理策略");
			put("30", "主机运维策略");
			put("31", "流量管理策略");
			put("32", "消息推送策略");
			put("33", "备份策略");
			put("34", "终端配置策略");
			put("35", "杀毒软件管理策略");
			put("36", "移动存储管理策略");
		}
	};

	/**
	 * 主审资产数据
	 */
	 @Scheduled(cron = "${ntds.asset.cron}")
	public void auditAssetTask() throws Exception {
		logger.warn("--------------定时主审资产数据同步开始-----------------------------");
		NTDSRequest request = new NTDSRequest();
		request.setUrl(assetAuditurl);
		request.setType(RequestTypeEnum.get);
		Map<String, String> map = new HashMap<>();
		map.put("where", "{'SearchCondition':{}}");
		request.setParam(map);
		List<Map<String, Object>> deviceList = null;
		for (int i = 0; i < tryCount; i++) {
			Map<String, Object> result = httpSyncRequest.getResult(request);
			if (result.get("DataSource") != null) {
				deviceList = (List<Map<String, Object>>) result.get("DataSource");
				break;
			}
		}
		if (deviceList == null) {
			throw new RuntimeException("------------获取全部设备失败---------------");
		}
		List<AuditAssetVO> auditAssetVOList = new ArrayList<>();
		for (Map<String, Object> device : deviceList) {
			AuditAssetVO assetVO = gson.fromJson(gson.toJson(device), AuditAssetVO.class);
			String runLevel = device.get("RunLevel") != null ? device.get("RunLevel").toString() : "";
			//值等于4或者2的话，表示安装了杀毒软件
			Integer installAntiVirusStatus = "2".equals(runLevel) || "4".equals(runLevel) ? 1 : 0;
			assetVO.setInstallAntiVirusStatus(installAntiVirusStatus);
			//根据上次时间判断客户是否在线,根据最后离开时间和当前时间比较
			assetVO.setClientStatus(assetVO.getClientUpLastTime().after(new Date()) ? 1 : 0);
			//todo 设备密级没有提供！
			//添加资产到集合
			auditAssetVOList.add(assetVO);
		}
		//同步数据入库
		assetService.updateAssetByNTDS(auditAssetVOList);
	}

	/**
	 * 策略统计任务类
	 */
	@Scheduled(cron = "${ntds.simpleSearch.cron}")
	public void strategyStatisticsTask() {
		logger.warn("--------------定时主审策略同步开始-----------------------------");
		//策略之前删除以前的,始终只要最新的数据
		strategyStatisticsRepository.deleteAll();
		NTDSRequest request = new NTDSRequest();
		request.setUrl(simpleSearchUrl);
		request.setType(RequestTypeEnum.get);
		Map<String, String> paramMap = new HashMap<>();
		//接口调用凭证
		paramMap.put("userGuid", getToken());
		//获取中蹙额的设备数量
		Long registerDeviceNum=getRegisterDeviceNum();
		List<StrategyStatistics> strategyStatisticsList = new ArrayList<>();
		//遍历循环 key-value形式的entry
		for (Map.Entry<String, String> entry : strategyMap.entrySet()) {
			paramMap.put("funcID", entry.getKey());
			request.setParam(paramMap);
			//最多尝试3次
			for (int i = 0; i < tryCount; i++) {
				Map<String, Object> result = httpSyncRequest.getResult(request);
				logger.info("-----code==" + result.get("Code"));
				//0是正常1异常
				if (0 == Integer.parseInt(result.get("Code").toString())) {
					StrategyStatistics strategyStatistics = new StrategyStatistics();
					//策略名称
					strategyStatistics.setStrategyName(entry.getValue());
					strategyStatistics.setGuid(UUIDUtils.get32UUID());
					//策略执行统计数量
					strategyStatistics.setStrategyNum(result.get("result") != null ? Long.parseLong(result.get("result").toString()) : 0);
					//下面是注册设备数量，都是一样的
					strategyStatistics.setRegisterDeviceNum(registerDeviceNum);
					strategyStatisticsList.add(strategyStatistics);
					break;
				}
			}
		}
		//最后将数据入库
		strategyStatisticsRepository.saveAll(strategyStatisticsList);
	}

	/**
	 * 获取调用的凭证
	 */
	public String getToken() {
		NTDSRequest request = new NTDSRequest();
		request.setUrl(loginUrl);
		request.setType(RequestTypeEnum.get);
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("userName", userName);
		TbConf tbConf = tbConfService.getOne("alarmdeal_ntds_login_password");
		if(null == tbConf){
			throw new RuntimeException("tb_conf表没有配置登陆密码(alarmdeal_ntds_login_password)");
		}
		paramMap.put("password", tbConf.getValue());
		request.setParam(paramMap);
		String token = "";
		for (int i = 0; i < tryCount; i++) {
			Map<String, Object> result = httpSyncRequest.getResult(request);
			//0是正常1异常
			if (0 == Integer.parseInt(result.get("Code").toString())) {
				token = result.get("result").toString();
				return token;
			}
		}
		throw new RuntimeException("--获取接口getToken调用失败了---");
	}

	/**
	 * 获取已经注册的设备数量
	 */
	public Long getRegisterDeviceNum() {
		GetRegisterDeviceRequest request = new GetRegisterDeviceRequest();
		request.setUrl(getStaticsIndexNumUrl);
		request.setType(RequestTypeEnum.get);
		Map<String, String> paramMap = new HashMap<>();
		//19表示已经注册的设备数量
		paramMap.put("terminalStatisticsTypeNum", "19");
		request.setParam(paramMap);
		Long registerDeviceNum = 0L;
		for (int i = 0; i < tryCount; i++) {
			registerDeviceNum = httpSyncRequest.getResult(request);
			if(registerDeviceNum!=null){
				return registerDeviceNum;
			}
		}
		throw new RuntimeException("--获取getRegisterDeviceNum接口调用失败了---");
	}
}
