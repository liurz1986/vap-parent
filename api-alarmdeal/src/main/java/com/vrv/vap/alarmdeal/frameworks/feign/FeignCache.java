package com.vrv.vap.alarmdeal.frameworks.feign;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseSysinfoServer;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.jpa.cache.GuavaCacheUtils;
import com.vrv.vap.jpa.spring.SpringUtil;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FeignCache {

	private static Logger logger = LoggerFactory.getLogger(FeignCache.class);

	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

	private static AdminFeign adminFeign;
	private static AuditFeign auditFegin;

	static {
		if (adminFeign == null) {
			adminFeign = SpringUtil.getBean(AdminFeign.class);
		}
		
		if (auditFegin == null) {
			auditFegin = SpringUtil.getBean(AuditFeign.class);
		}
	}

	public List<BaseKoalOrg> getOrgPage(Map<String, String> param) {

		String key = "authFeign_getOrgPage_" + DigestUtils.md5DigestAsHex(gson.toJson(param).getBytes());
		return GuavaCacheUtils.getValue(key, param, (params) -> {
			VList<BaseKoalOrg> pageDict = adminFeign.getOrgPage((Map<String, String>) params);
			if (pageDict != null && pageDict.getList() != null) {
				List<BaseKoalOrg> data = pageDict.getList();

				return data;
			}
			return null;
		});
	}

	public List<BaseDictAll> getPageDict(Map<String, Object> param) {
		String key = "authFeign_getPageDict_" + DigestUtils.md5DigestAsHex(gson.toJson(param).getBytes());
		return GuavaCacheUtils.getValue(key, param, (params) -> {
			VList<BaseDictAll> pageDict = adminFeign.getPageDict((Map<String, Object>) params);
			if (pageDict != null && pageDict.getList() != null) {
				List<BaseDictAll> data = pageDict.getList();

				return data;
			}
			return null;
		});
	}

	// public VData<List<BasePersonZjg>> getAllBasePersonZjg() ;

	public List<BasePersonZjg> getAllBasePersonZjg() {
		String name = "authFeign_getAllBasePersonZjg";

		return GuavaCacheUtils.getValue(name, () -> {

			VData<List<BasePersonZjg>> allBasePersonZjg = adminFeign.getAllPerson();
			if (allBasePersonZjg != null && allBasePersonZjg.getData() != null) {
				List<BasePersonZjg> data = allBasePersonZjg.getData();
				return data;
			}

			return null;
		});

	}

	public static User getUserById(String userId) {
		String key = "authFeign_getUserById_" + userId;

		return GuavaCacheUtils.getValue(key, userId, (params) -> {
			ResultObjVO<User> userById = adminFeign.getUserById(Integer.parseInt((String) params));
			if (userById != null && userById.getData() != null) {
				com.vrv.vap.alarmdeal.frameworks.contract.user.User data = userById.getData();
				return data;
			}
			return null;
		});
	}

	public static List<Role> getRoleById(Map<String, Object> param) {
		String key = "authFeign_getRoleById_" + DigestUtils.md5DigestAsHex(gson.toJson(param).getBytes());

		return GuavaCacheUtils.getValue(key, param, (params) -> {

			ResultObjVO<List<Role>> roleById = adminFeign.getRoleById((Map<String, Object>) params);
			if (roleById != null && roleById.getData() != null && !roleById.getData().isEmpty()) {
				List<Role> data = roleById.getData();
				return data;
			}
			return null;
		});

	}

	public static List<BaseKoalOrg> orgByUserId(String userId) {
		VData<List<BaseKoalOrg>> orgByUserId = adminFeign.orgByUserId(userId);
		if (orgByUserId != null && orgByUserId.getData() != null && !orgByUserId.getData().isEmpty()) {
			List<BaseKoalOrg> data = orgByUserId.getData();
			return data;
		}
		return new ArrayList<>();
	}
	
	
	public static List<BaseSysinfoServer> appServer(Map<String, Object> param) {

		String key = "auditFegin_appServer_" + DigestUtils.md5DigestAsHex(gson.toJson(param).getBytes());;

		return GuavaCacheUtils.getValue(key, param, (params) -> {
			ResultObjVO<List<BaseSysinfoServer>> appServers = auditFegin.sysServer(param);
			if (appServers != null && appServers.getList() != null) {
				List<BaseSysinfoServer> data = appServers.getList();
				return data;
			}
			return null;
		}

		);

	}
	
	
}
