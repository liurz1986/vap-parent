package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.RoleResourceMapper;
import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.RoleResource;
import com.vrv.vap.admin.service.RoleResourceService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RoleResourceServiceImpl extends BaseServiceImpl<RoleResource> implements RoleResourceService {
    private static Logger logger = LoggerFactory.getLogger(RoleResourceServiceImpl.class);
    @Resource
    private RoleResourceMapper roleresourceMapper;

    private Set<String> buildRole(Set<String> roles, List<com.vrv.vap.admin.model.Resource> resources, String rootType, boolean isAdmin) {
        for (com.vrv.vap.admin.model.Resource resource : resources) {
            if (rootType.equals(resource.getPuid())) {
                if (resource.getType() == 1) {
                    if (StringUtils.isNotEmpty(resource.getPath())) {
                        logger.info("===加载目录菜单===" + LogForgingUtil.validLog(resource.getPath()));
                        roles.add(resource.getPath());
                    }
                    buildRole(roles, resources, resource.getUid(), false);
                    // 展示平台链接或者大屏平台，必须为2
                } else if (resource.getType() == 2) {
                    String path = resource.getPath();
                    if (StringUtils.isNotEmpty(resource.getPath()) && resource.getPath().contains("#")) {
                        path = resource.getPath().split("#")[0];
                    }
                    logger.info("===加载功能菜单===" + LogForgingUtil.validLog(path));
                    roles.add(path);
                }
            }
        }
        return roles;
    }

    @Override
    public Set<String> buildRole(List<com.vrv.vap.admin.model.Resource> resources) {
        Set<String> roleSet = new HashSet<>();
        //0:管理平台，-1：展示平台,-2:大屏平台
        roleSet = this.buildRole(roleSet, resources, "0", true);
        roleSet = this.buildRole(roleSet, resources, "-1", false);
        roleSet = this.buildRole(roleSet, resources, "-2", false);
        return roleSet;
    }

    @Override
    public boolean managerRoleResource(int roleId, String[] addList, String[] delList) {
        List<RoleResource> list = new ArrayList<>();
        if (addList.length > 0) {
            for (String add : addList) {
                RoleResource roleResource = new RoleResource();
                roleResource.setRoleId(roleId);
                roleResource.setResourceId(Integer.parseInt(add));
                list.add(roleResource);
            }
            this.save(list);
        }
        if (delList.length > 0) {
            Example example = new Example(RoleResource.class);
            example.createCriteria().andEqualTo("roleId", roleId)
                    .andIn("resourceId", Arrays.asList(delList));
            this.roleresourceMapper.deleteByExample(example);
        }
        return true;
    }

    @Override
    public List<App> getRoleApps(List<Integer> roleIds) {
        return roleresourceMapper.queryRoleApps(roleIds);
    }

    @Override
    public List<com.vrv.vap.admin.model.Resource> getRoleRules(List<Integer> roleIds) {
        return roleresourceMapper.queryRoleRules(roleIds);
    }

    @Override
    public List<com.vrv.vap.admin.model.Resource> getRoleWidgets(List<Integer> roleIds) {
        return roleresourceMapper.queryRoleWidgets(roleIds);
    }


    @Override
    public int queryCountByResource(String resourceIds) {
        List<String> s = Arrays.asList(resourceIds.split(","));
        List<Integer> i = new ArrayList<>();
        for (String ss: s) {
            i.add(Integer.valueOf(ss));
        }
        return roleresourceMapper.queryCountByResource(i);
    }


    @Override
    public void deleteByRoleIds(String[] ids) {
        Example example = new Example(RoleResource.class);
        example.createCriteria().andIn("roleId", Arrays.asList(ids));
        roleresourceMapper.deleteByExample(example);
    }

    /**
     * 删除所有角色权限
     */
    @Override
    public void deleteAllRoleResource() {
        List<RoleResource> roleResourceList = roleresourceMapper.selectAll();
        if (CollectionUtils.isNotEmpty(roleResourceList)) {
            for (RoleResource roleResource : roleResourceList) {
                roleresourceMapper.deleteByPrimaryKey(roleResource.getId());
            }
        }
    }


    @Override
    public void deleteByResourceIds(String[] ids) {
        Example example = new Example(RoleResource.class);
        example.createCriteria().andIn("resourceId", Arrays.asList(ids));
        roleresourceMapper.deleteByExample(example);
    }
}
