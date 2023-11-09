package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.util.ThreePowersUtil;
import com.vrv.vap.admin.mapper.ResourceMapper;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.service.ResourceService;
import com.vrv.vap.admin.service.RoleService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by CodeGenerator on 2018/03/21.
 */
@Service
@Transactional
public class ResourceServiceImpl extends BaseServiceImpl<Resource> implements ResourceService {

    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private SystemConfigService systemConfigService;

    @Autowired
    private RoleService roleService;

    @Override
    public List<Resource> loadResource(int roleId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        Map<String,Object> param = new HashMap<>();
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            Short confEnable = systemConfigService.getThreePowerEnable();
            param.put("roleId",roleId);
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                if (!CollectionUtils.isEmpty(roleIds)) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    if (role != null) {
                        List<Integer> threePowers = ThreePowersUtil.getRolePowers(role.getThreePowers());
                        param.put("threePowers",threePowers);
                        param.put("threePowerConf",confEnable);
                    }
                }
            }
        }
        Map<String,Object> query = new HashedMap();
        param.keySet().stream().forEach(p->{
            query.put(p,param.get(p));
        });
        return resourceMapper.queryByRoleId(query);
    }

    @Override
    public List<Resource> loadResourceByRoleIds(List<Integer> roleIds) {
        return resourceMapper.queryByRoleIds(roleIds);
    }

    /**
     * 重写 findAll 方法，过滤掉 disabled 的项
     */
    @Override
    public List<Resource> findAll() {
        Example example = new Example(Resource.class);
        example.createCriteria()
                .andNotEqualTo("disabled", 1)
                .andNotEqualTo("type",3)
                .andNotEqualTo("type",4);
        return this.findByExample(example);
    }


    @Override
    public Resource findResourceByUid(String uid) {
        Example example = new Example(Resource.class);
        example.createCriteria().andEqualTo("uid",uid);
        List<Resource> resourceList = resourceMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(resourceList)){
            return resourceList.get(0);
        }
        return null;
    }

    @Override
    public int disableResource(String resourceId) {
        return resourceMapper.disableResource(resourceId);
    }

    @Override
    public int enableResource(String resourceId) {
        return resourceMapper.enableResource(resourceId);
    }
}
