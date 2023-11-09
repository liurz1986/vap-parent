package com.vrv.vap.admin.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.mapper.ResourceApiMapper;
import com.vrv.vap.admin.mapper.ServiceApiMapper;
import com.vrv.vap.admin.model.ResourceApi;
import com.vrv.vap.admin.model.RoleResource;
import com.vrv.vap.admin.service.ResourceApiService;
import com.vrv.vap.admin.service.RoleResourceService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lilang
 * @date 2022/10/9
 * @description
 */
@Service
@Transactional
public class ResourceApiServiceImpl extends BaseServiceImpl<ResourceApi> implements ResourceApiService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceApiServiceImpl.class);

    @Resource
    ResourceApiMapper resourceApiMapper;

    @Resource
    ServiceApiMapper serviceApiMapper;

    @Autowired
    RoleResourceService roleResourceService;

    @Autowired
    private StringRedisTemplate redisTpl;

    private static final String ROLE_API = "_ROLE_API";

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    public boolean manageResourceApi(Integer resourceId, String[] addList, String[] delList) {
        List<ResourceApi> list = new ArrayList<>();
        if (addList.length > 0) {
            for (String add : addList) {
                ResourceApi resourceApi = new ResourceApi();
                resourceApi.setResourceId(resourceId);
                resourceApi.setApiId(Integer.valueOf(add));
                list.add(resourceApi);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                this.save(list);
            }
        }
        if (delList.length > 0) {
            Example example = new Example(ResourceApi.class);
            example.createCriteria().andEqualTo("resourceId",resourceId)
                    .andIn("apiId", Arrays.asList(delList));
            this.resourceApiMapper.deleteByExample(example);
        }
        // 更新资源菜单对应的角色接口缓存
        List<Integer> roleIds = this.getRoleIds(resourceId);
        this.cacheRoleApiList(roleIds);
        return true;
    }

    private List<Integer> getRoleIds(Integer resourceId) {
        List<Integer> roleIds = new ArrayList<>();
        Example example = new Example(RoleResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("resourceId",resourceId);
        List<RoleResource> roleResourceList = roleResourceService.findByExample(example);
        if (CollectionUtils.isNotEmpty(roleResourceList)) {
            roleResourceList.stream().forEach(item -> roleIds.add(item.getRoleId()));
        }
        return roleIds;
    }

    private void cacheRoleApiList(List<Integer> roleIds) {
        for (Integer roleId : roleIds) {
            List<Integer> apiList = serviceApiMapper.getApiListByRoleId(roleId);
            if (CollectionUtils.isNotEmpty(apiList)) {
                String key = ROLE_API + roleId + "";
                logger.info("更新角色：" + roleId + "对应API接口列表：" + apiList);
                redisTpl.opsForValue().set(key, gson.toJson(apiList));
            }
        }
    }
}
