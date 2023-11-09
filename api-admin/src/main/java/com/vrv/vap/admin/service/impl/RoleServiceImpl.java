package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.mapper.RoleMapper;
import com.vrv.vap.admin.model.RoleOrg;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.service.RoleOrgService;
import com.vrv.vap.admin.service.RoleService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
@Service
@Transactional
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService{

    @Resource
    RoleMapper roleMapper;

    @Autowired
    RoleOrgService roleOrgService;

//    @Override
//    public List<Role> findByExample(Example example) {
////        example.and().andNotEqualTo("id", Const.SYSTEM_ROLE);
//        return super.findByExample(example);
//    }

    /**
     * 删除所有角色
     */
    public void deleteAllRole() {
        List<Role> roleList = roleMapper.selectAll();
        if (CollectionUtils.isNotEmpty(roleList)) {
            for (Role role : roleList) {
                roleMapper.deleteByPrimaryKey(role.getId());
            }
        }
    }

    @Override
    public Role findRoleByGuid(String guid) {
        Example example = new Example(Role.class);
        example.createCriteria().andEqualTo("guid",guid);
        List<Role> list =  roleMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return  list.get(0);
        }
        return null;
    }

    @Override
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertRoleList(List<Role> roleList) {
        return roleMapper.insertList(roleList);
    }

    @Override
    public int updateRoleOrg(Role role) {
        // 修改前的组织机构
        List<RoleOrg> existedOrgs = roleOrgService.findByProperty(RoleOrg.class,"roleId",role.getId());
        Set<Integer> already = new HashSet<>();
        for (RoleOrg orgRole : existedOrgs) {
            already.add(orgRole.getId());
        }
        // 修改后的组织机构
        Set<String> newOrg = new HashSet<>();
        if (StringUtils.isNotBlank(role.getOrgId())) {
            for (String orgId : role.getOrgId().split(",")) {
                newOrg.add(orgId);
            }
        }
        // 修改前后取交集，统计新增和删除的组织机构
        List<String> stroge = (List<String>) CollectionUtils.intersection(already, newOrg);
        List<RoleOrg> toAdd = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        for (Integer orgId : already) {
            if (!stroge.contains(orgId)) {
                toDelete.add(orgId);
            }
        }
        for (String orgId : newOrg) {
            if (!stroge.contains(orgId)) {
                RoleOrg orgRole = new RoleOrg();
                orgRole.setRoleId(role.getId());
                orgRole.setOrgId(Integer.valueOf(orgId));
                toAdd.add(orgRole);
            }
        }
        // 保存数据
        int result = roleMapper.updateByPrimaryKeySelective(role);
        if (result == 1) {
            if (toAdd.size() > 0) {
                roleOrgService.save(toAdd);
            }
            if (toDelete.size() > 0) {
                roleOrgService.deleteByIds(StringUtils.join(toDelete.toArray(), ","));
            }
        }
        return result;
    }

    @Override
    public List<Role> getBusinessAndOperationRole(String roleId,String dealType) {
        List<Role> roleList = new ArrayList<>();
        boolean exist = false;
        String[] roleIds = roleId.split(",");
        for (String id : roleIds) {
            Role role = roleMapper.selectByPrimaryKey(Integer.valueOf(id));
            if (role != null) {
                String roleCode = role.getCode();
                if (Const.SECRET_MGR.equals(roleCode)) {
                    exist = true;
                }
            }
        }
        if (exist) {
            Role businessRole = getRoleByCode(Const.BUSINESS_MGR);
            if (businessRole != null) {
                roleList.add(businessRole);
            }
            Role operationRole = getRoleByCode(Const.OPERATION_MGR);
            if (operationRole != null) {
                roleList.add(operationRole);
            }
            //20231017调整：策略配置选择需要增加保密角色，督促不需要，用的新接口，这样避免改原来的接口出现新的bug
            if("strategy".equals(dealType)){
                Role secretRoleByCode = getRoleByCode(Const.SECRET_MGR);
                if(secretRoleByCode!=null){
                    roleList.add(secretRoleByCode);
                }
            }
        }
        return roleList;
    }

    public Role getRoleByCode(String roleCode) {
        Role bRole = new Role();
        bRole.setCode(roleCode);
        Role role = roleMapper.selectOne(bRole);
        return role;
    }
}
