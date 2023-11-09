package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.RoleOrgMapper;
import com.vrv.vap.admin.model.RoleOrg;
import com.vrv.vap.admin.service.RoleOrgService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lilang
 * @date 2021/8/23
 * @description
 */
@Service
@Transactional
public class RoleOrgServiceImpl extends BaseServiceImpl<RoleOrg> implements RoleOrgService {


    @Resource
    private RoleOrgMapper roleOrgMapper;

    @Override
    public void saveOrgRoles(String orgId, Integer roleId) {
        if (StringUtils.isNotEmpty(orgId)) {
            String[] orgIds = orgId.split(",");
            List<RoleOrg> orgRoleList = new ArrayList<>();
            for (String id : orgIds) {
                RoleOrg orgRole = new RoleOrg();
                orgRole.setOrgId(Integer.valueOf(id));
                orgRole.setRoleId(roleId);
                orgRoleList.add(orgRole);
            }
            roleOrgMapper.insertList(orgRoleList);
        }
    }

    @Override
    public void deleteByRoleIds(String[] roleIds) {
        Example example = new Example(RoleOrg.class);
        example.createCriteria().andIn("roleId", Arrays.asList(roleIds));
        roleOrgMapper.deleteByExample(example);
    }
}
