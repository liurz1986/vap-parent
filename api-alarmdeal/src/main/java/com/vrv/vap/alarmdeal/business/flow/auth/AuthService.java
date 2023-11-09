package com.vrv.vap.alarmdeal.business.flow.auth;

import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;




public interface AuthService {
	
	
	/**
	 * 通过角色code获得用户
	 * @param role
	 * @return
	 */
	public List<String> getUsersByRole(String role);
	/**
	 * 通过机构组织获得用户
	 * @param org
	 * @return
	 */
	public List<String> getUsersByOrg(String org);
	/**
	 * 通过组织结构和角色获得当前的用户(提供)
	 * @param org
	 * @param role
	 * @return
	 */
	public List<String> getUsersByOrgAndRole(String org, String role);
	
	
	/**
	 * 根据用户ID获得用户信息
	 * @param userId
	 * @return
	 */
	public User getUserInfoByUserId(String userId);
	
	/**
	 * 根据roleCode获得角色信息
	 * @param roleCode
	 * @return
	 */
	public Role getRoleInfoByRoleCode(String roleCode);
	
	
	/**
	 * 根据userId获得组织结构的信息
	 */
	public BaseKoalOrg getOrgByUser(String userId);
	
	/**
	 * 获得组织结构树
	 * @param id
	 * @return
	 */
	public List<BaseKoalOrg> getOrgTree(String id);
	
	/**
	 * 通过IP获得组织结构
	 * @param ips
	 * @return
	 */
	public Set<String> getOrgByIP(String ips);
	
	/**
	 * 获得当前登陆用户所在组织的领导
	 * @param orgId
	 * @return
	 */
	public List<String> getOrgLeader(String orgId);
	
	/**
	 * 通过当前登陆用户上级指定成员
	 * @param orgId
	 * @param isLeader
	 * @return
	 */
	public List<String> getUpOrgMemebers(String orgId,String orgCode);
	
	/**
	 * 根据安全域code获得对应的安全域绑定的对应的人
	 * @param map
	 * @return
	 */
	public List<String> byCode(Map<String,Object> map);


	/**
	 * 获取短信
	 */
	public  Map<String,Object> shortMessage(Map<String,Object> map);

	
	
	/**
	 * 根据用户Id获得对应的安全域
	 * @return
	 */
	public List<BaseSecurityDomain> byUserId(String cookie);
	
	
	/**
	 * 获得单个安全域信息
	 * @param code
	 * @return
	 */
	public BaseSecurityDomain singleBySecCode(String code);
	
	/**
	 * 根据code获得角色信息
	 * @param code
	 * @return
	 */
	public Role getRoleInfo(String code);
	
}
