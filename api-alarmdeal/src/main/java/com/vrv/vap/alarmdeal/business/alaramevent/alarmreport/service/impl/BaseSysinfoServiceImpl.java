package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfo;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfoServer;
import com.vrv.vap.alarmdeal.frameworks.feign.AuditFeign;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月20日 10:12
 */
@Service
public class BaseSysinfoServiceImpl implements BaseSysinfoService {

	private static Logger logger = LoggerFactory.getLogger(BaseSysinfoService.class);
	@Autowired
	AuditFeign auditFeign;

	private static String key_Data_App = "BaseSysinfos";

	private static String key_Data_AppIp = "BaseSysinfoIps";

	static Integer cacheTime = 5 * 60;// 秒

	@Override
	public User getCurrentUser() {
		return SessionUtil.getCurrentUser();
	}

	@Override
	public void addUserPermissions(AnalysisVO analysisVO) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			throw new RuntimeException("获取用户登陆信息失败！");
		}

		if (SessionUtil.getCurrentUser() != null && SessionUtil.getauthorityType()) {
			logger.debug("本次查询开启权限验证");
			List<String> userDomainCodes = SessionUtil.getUserDomainCodes();

			if (userDomainCodes == null || userDomainCodes.size() == 0) {
				analysisVO.setDstAreaCode("@#$@#$@#");// 使查不出数据来
			} else {
				logger.debug("用户管理的安全域集合：");
				//logger.debug(userDomainCodes);
				analysisVO.setDstAreaCode(String.join(",", userDomainCodes));
			}
		}
	}

	/**
	 * 补全地图数据查询条件
	 *
	 * @param mapType
	 * @param fromAreaName
	 * @param toAreaName
	 * @param analysisVO
	 * @return
	 */
	public Result<Object> addWorldMapQuerys(Integer mapType, String fromAreaName, String toAreaName, AnalysisVO analysisVO) {
		String nameStr = "";
		switch (mapType) {
			case 1:
				nameStr = ".continent";
				break;
			case 2:
				nameStr = ".country";
				break;
			case 3:
				nameStr = ".province";
				break;
			case 4:
				nameStr = ".city";
				break;
			default:
				return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "地图显示范围传参异常");
		}


		if ("*".equals(fromAreaName) || "*".equals(toAreaName)) {
			if (!"*".equals(fromAreaName)) {
				analysisVO.setEsQuery(QueryCondition_ES.eq("srcWorldMapName" + nameStr, fromAreaName));
			} else if (!"*".equals(toAreaName)) {
				analysisVO.setEsQuery(QueryCondition_ES.eq("dstWorldMapName" + nameStr, toAreaName));
			}
		} else {
			analysisVO.setEsQuery(QueryCondition_ES.and(
					QueryCondition_ES.eq("srcWorldMapName" + nameStr, fromAreaName)
					, QueryCondition_ES.eq("dstWorldMapName" + nameStr, toAreaName)));
		}

		return null;
	}


	@Override
	public void addUserAssetPermissions(AnalysisVO analysisVO) {
		User currentUser = getCurrentUser();
		if (currentUser == null) {
			throw new RuntimeException("获取用户登陆信息失败！");
		}

		if (SessionUtil.getCurrentUser() != null && SessionUtil.getauthorityType()) {
			logger.debug("本次查询开启权限验证");
			List<String> userDomainCodes = SessionUtil.getUserDomainCodes();

			if (userDomainCodes == null || userDomainCodes.size() == 0) {
				analysisVO.setDstAreaCode("@#$@#$@#");// 使查不出数据来
			} else {
				logger.debug("用户管理的安全域集合：");
				analysisVO.setDstAreaCode(String.join(",", userDomainCodes));
			}
		}
	}

	@Override
	public void addUserAppPermissions(AnalysisVO analysisVO) {

		analysisVO.setLinkApp(true);

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			throw new RuntimeException("获取用户登陆信息失败！");
		}

		if (SessionUtil.getCurrentUser() != null && SessionUtil.getauthorityType()) {
			logger.debug("本次查询开启权限验证");
			List<String> userDomainCodes = SessionUtil.getUserDomainCodes();

			if (userDomainCodes == null || userDomainCodes.size() == 0) {
				analysisVO.setDstAreaCode("@#$@#$@#");// 使查不出数据来
			} else {
				logger.debug("用户管理的安全域集合：");
				//logger.debug(userDomainCodes);
				analysisVO.setDstAreaCode(String.join(",", userDomainCodes));
			}
		}
	}

	@Override
	public void setTimes(String timeType, AnalysisVO analysisVO) {
		switch (timeType) {
			case "24h":
				analysisVO.setStime(DateUtil.format(DateUtils.addHours(new Date(), -23), "yyyy-MM-dd HH:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "today":
				analysisVO.setStime(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "week":
				analysisVO.setStime(DateUtil.format(DateUtils.addDays(new Date(), -6), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "month":
				analysisVO.setStime(DateUtil.format(DateUtils.addMonths(new Date(), -1), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "15day":
				analysisVO.setStime(DateUtil.format(DateUtils.addDays(new Date(), -14), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "7day":
				analysisVO.setStime(DateUtil.format(DateUtils.addDays(new Date(), -6), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "30day":
				analysisVO.setStime(DateUtil.format(DateUtils.addDays(new Date(), -29), "yyyy-MM-dd 00:00:00"));
				analysisVO.setEtime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				break;
			case "all"://查询所有数据，无时间限定

				break;
			default:
				throw new RuntimeException("时间类型传参异常");
		}
	}


	/**
	 * Object 转List
	 *
	 * @param obj
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> castList(Object obj, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		if (obj instanceof List<?>) {
			for (Object o : (List<?>) obj) {
				result.add(clazz.cast(o));
			}
			return result;
		}
		return null;
	}

	private List<BaseSysinfo> getBaseSysinfos() {
		String key = key_Data_App;
		String sessionId = SessionUtil.getSessionId();
		if (!StringUtils.isEmpty(sessionId)) {
			key = key_Data_App + "_" + sessionId;
		}

		Object object = CommomLocalCache.get(key);
		if (object == null) {
			return null;
		}
		try {
			return castList(object, BaseSysinfo.class);
		} catch (Exception e) {
			logger.error("数据转换发生错误：", e);
			return null;
		}

	}

	private List<String> getBaseSysinfoIps() {
		String key = key_Data_AppIp;
		String sessionId = SessionUtil.getSessionId();
		if (!StringUtils.isEmpty(sessionId)) {
			key = key_Data_AppIp + "_" + sessionId;
		}

		Object object = CommomLocalCache.get(key);
		if (object == null) {
			return null;
		}
		try {
			return castList(object, String.class);
		} catch (Exception e) {
			logger.error("数据转换发生错误：", e);
			return null;
		}

	}

	private void setBaseSysinfos(List<BaseSysinfo> items) {
		try {
			String key = key_Data_App;
			String sessionId = SessionUtil.getSessionId();
			if (!StringUtils.isEmpty(sessionId)) {
				key = key_Data_App + "_" + sessionId;
			}
			CommomLocalCache.put(key, items, cacheTime, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error("数据缓存发生错误：", e);
		}
	}

	private void setBaseSysinfoIps(List<String> items) {
		try {
			String key = key_Data_AppIp;
			String sessionId = SessionUtil.getSessionId();
			if (!StringUtils.isEmpty(sessionId)) {
				key = key_Data_AppIp + "_" + sessionId;
			}
			CommomLocalCache.put(key, items, cacheTime, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error("数据缓存发生错误：", e);
		}
	}

	@Override
	public synchronized List<BaseSysinfo> getAllApplication() {
		if (getCurrentUser() == null) {
			logger.error("session为空，停止继续查询");
			return new ArrayList<BaseSysinfo>();
		}
		Date now = new Date();

		List<BaseSysinfo> baseSysinfos = getBaseSysinfos();
		if (baseSysinfos == null || baseSysinfos.size() == 0) {
			try {
				logger.info("尝试请求当前用户的有权限的应用系统");
				int toatl = 0;
				int start = 0;
				List<BaseSysinfo> allApplication = new ArrayList<>();

				while (start <= toatl) {
					Map<String, Object> param = new HashMap<>();
					param.put("start", start);
					param.put("count", 100);
					PageRes<BaseSysinfo> page = auditFeign.getAllAppSystem(param);
					if ("0".equals(page.getCode())) {
						allApplication.addAll(page.getList());
						toatl = page.getTotal().intValue();
						logger.info("分页请求所有应用系统数据成功,本次下载" + page.getList().size() + "/" + toatl + "  已完成"
								+ allApplication.size() + "/" + toatl);
					} else {
						toatl = -1;
						logger.error("发生错误：" + page.getMessage());
					}
					start += 100;
				}
				setBaseSysinfos(allApplication);
			} catch (Exception e) {
				logger.error("分页请求所有应用系统数据发生异常", e);
			}
		}

		return getBaseSysinfos();
	}

	/**
	 * 根据对应的systemId获得对应的ip集合
	 *
	 * @param systemIds
	 * @return
	 */
	@Override
	public Set<String> getIpList(String[] systemIds) {
		Set<String> ipList = new HashSet<>();
		for (String systemId : systemIds) {
			Map<String, Object> map = new HashMap<>();
			map.put("systemId", systemId);
			ResultObjVO<List<BaseSysinfo>> sysinfo = auditFeign.sysinfo(map);
			List<BaseSysinfo> sysInfoList = sysinfo.getList();
			for (BaseSysinfo baseSysinfo : sysInfoList) {
				Integer id = baseSysinfo.getId();
				Map<String, Object> serverMap = new HashMap<>();
				serverMap.put("id", id);
				ResultObjVO<List<BaseSysinfoServer>> sysServer = auditFeign.sysServer(serverMap);
				List<BaseSysinfoServer> list = sysServer.getList();
				for (BaseSysinfoServer baseSysinfoServer : list) {
					ipList.add(baseSysinfoServer.getIp());
				}
			}
		}
		return ipList;
	}


	@Override
	public List<String> getIpList(String systemId) {
		List<String> ipList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("systemId", systemId);
		ResultObjVO<List<BaseSysinfo>> sysinfo = auditFeign.sysinfo(map);
		List<BaseSysinfo> sysInfoList = sysinfo.getList();
		for (BaseSysinfo baseSysinfo : sysInfoList) {
			Integer id = baseSysinfo.getId();
			Map<String, Object> serverMap = new HashMap<>();
			serverMap.put("sysId", id);
			ResultObjVO<List<BaseSysinfoServer>> sysServer = auditFeign.sysServer(serverMap);
			List<BaseSysinfoServer> list = sysServer.getList();
			for (BaseSysinfoServer baseSysinfoServer : list) {
				ipList.add(baseSysinfoServer.getIp());
			}
		}

		return ipList;
	}

	public synchronized List<String> getAllAppIp() {
		if (getCurrentUser() == null) {
			logger.error("session为空，停止继续查询");
			return new ArrayList<String>();
		}
		Date now = new Date();

		List<String> baseSysinfoIps = getBaseSysinfoIps();
		if (baseSysinfoIps == null || baseSysinfoIps.size() == 0) {
			try {
				logger.info("尝试请求当前用户的有权限的应用系统IP信息");

				List<String> allAppIps = new ArrayList<>();
				List<BaseSysinfo> allApplication = getAllApplication();
				for (BaseSysinfo baseSysinfo : allApplication) {

					String ips = baseSysinfo.getIps();
					if (StringUtils.isNotEmpty(ips)) {
						allAppIps.addAll(Arrays.asList(ips.split(",")));
					}
				}
				logger.info("当前用户管理ip数量为：" + allAppIps.size());
				setBaseSysinfoIps(allAppIps);
			} catch (Exception e) {
				logger.error("分页请求应用系统IP信息数据发生异常", e);
			}
		} else {
			logger.info("当前用户管理ip数量为：" + baseSysinfoIps.size());
		}

		return getBaseSysinfoIps();
	}
}
