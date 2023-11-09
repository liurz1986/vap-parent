package com.vrv.vap.alarmdeal.business.flow.auth.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.flow.auth.AuthService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.OrgLeaderQuery;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.OrgUserQuery;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.Result;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
/**
 * 权限认证的实现类
 * @author wd-pc
 *
 */
@Service
public class AuthServiceImpl implements AuthService {

	private static Logger logger   = LoggerFactory.getLogger(AuthServiceImpl.class);
	@Autowired
	private AdminFeign authFeign;
	
	@Override
	public List<String> getUsersByRole(String role) {
		List<String> list=new ArrayList<>();
		logger.info("roleCode的值:{}",role);
		VData<List<User>> result = authFeign.getUserByRoleCode(role);
		List<User> userList = result.getData();
		for (User user : userList){
			list.add(String.valueOf(user.getId()));
		}
		return list;
	}

	@Override
	public List<String> getUsersByOrg(String org) {
		List<String> list = new ArrayList<>();
		list.add("a:"+org);
		list.add("b:"+org);
		list.add("c:"+org);
		list.add("d:"+org);
		return list;
	}

	@Override
	public List<String> getUsersByOrgAndRole(String org, String role) {
		OrgUserQuery orgUserQuery = new OrgUserQuery();
		orgUserQuery.setOrgId(Integer.valueOf(org));
		orgUserQuery.setRoleCode(role);
		Result<User> users = authFeign.users(orgUserQuery);
		List<User> data = users.getData();
		List<String> list = new ArrayList<>();
		for (User user : data) {
			list.add(user.getId().toString());
		}
		return list;
	}

	@Override
	public User getUserInfoByUserId(String userId) {
		ResultObjVO<User> userById = authFeign.getUserById(Integer.valueOf(userId));
		User user = userById.getData();
		return user;
	}

	@Override
	public BaseKoalOrg getOrgByUser(String userId) {
		VData<BaseKoalOrg> byUser = authFeign.getOrgByUser(userId);
		BaseKoalOrg BaseKoalOrg = byUser.getData();
		return BaseKoalOrg;
	}

	@Override
	public List<BaseKoalOrg> getOrgTree(String code) {
		VData<List<BaseKoalOrg>> orgTree = authFeign.getOrgChildren(code);
		List<BaseKoalOrg> list = orgTree.getData();
		return list;
	}

	@Override
	public Set<String> getOrgByIP(String ips) {
		Map<String,String> map = new HashMap<>();
		Set<String> set = new HashSet<>();
		String[] ipArr = ips.split(",");
		for (String ip : ipArr){
			map.put("ip",ip);
			ResultObjVO<BaseKoalOrg> res = authFeign.byIp(map);
			BaseKoalOrg BaseKoalOrg = res.getData();
			set.add(String.valueOf(BaseKoalOrg.getUuId()));
		}
		
		return set;
	}

	@Override
	public List<String> getOrgLeader(String orgId) {
        List<String> list = new ArrayList<>();
        OrgLeaderQuery orgLeaderQuery = new OrgLeaderQuery();
        orgLeaderQuery.setIsLeader(1);
        orgLeaderQuery.setCode(orgId);
        Result<User> result = authFeign.members(orgLeaderQuery);
        List<User> data = result.getData();
        for (User user : data) {
        	list.add(String.valueOf(user.getId()));
		}
		return list;
	}

	@Override
	public List<String> getUpOrgMemebers(String orgId,String orgCode) {
		List<String> list = new ArrayList<>();
        OrgLeaderQuery orgLeaderQuery = new OrgLeaderQuery();
        orgLeaderQuery.setIsLeader(1);
        orgLeaderQuery.setOrgId(orgId);
        orgLeaderQuery.setCode(orgCode);
        Result<User> result = authFeign.upMembers(orgLeaderQuery);
        List<User> data = result.getData();
        for (User user : data) {
        	list.add(String.valueOf(user.getId()));
		}
		return list;
	}

	@Override
	public List<String> byCode(Map<String, Object> map) {
		List<String> list = new ArrayList<>();
		Result<User> result = authFeign.byCode(map);
		 List<User> data = result.getData();
	        for (User user : data) {
	        	list.add(String.valueOf(user.getId()));
			}
		return list;
	}
	@Override
	public  Map<String,Object> shortMessage(Map<String,Object> map){
		return authFeign.shortMessage(map);
	}

	@Override
	public List<BaseSecurityDomain> byUserId(String cookie) {
		Result<BaseSecurityDomain> secDomains = authFeign.secDomainbyUserId(cookie);
		Gson gson = new Gson();
		logger.info("安全域返回信息：{}", gson.toJson(secDomains));
		List<BaseSecurityDomain> list = secDomains.getData();
		return list;
	}

	@Override
	public Role getRoleInfoByRoleCode(String roleCode) {
		ResultObjVO<Role> resultObjVO = authFeign.getRoleInfo(roleCode);
		Role role = resultObjVO.getData();
		return role;
	}

	@Override
	public BaseSecurityDomain singleBySecCode(String code) {
		VData<BaseSecurityDomain> singleSec = authFeign.getOneDomainByCode(code);
		BaseSecurityDomain baseSecurityDomain = singleSec.getData();
		return baseSecurityDomain;
	}

	@Override
	public Role getRoleInfo(String code) {
		ResultObjVO<Role> roleInfo = authFeign.getRoleInfo(code);
		Role role = roleInfo.getData();
		return role;
	}

	

}
