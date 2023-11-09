package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.UserOrgMapper;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.model.UserOrg;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.RoleService;
import com.vrv.vap.admin.service.UserOrgService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2021/8/23
 * @description
 */
@Service
@Transactional
public class UserOrgServiceImpl extends BaseServiceImpl<UserOrg> implements UserOrgService {

    @Resource
    private UserOrgMapper userOrgMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Override
    public void saveOrgUsers(String orgIds,Integer userId) {
        if(StringUtils.isNotEmpty(orgIds)){
            List<UserOrg> orgUserList = new ArrayList<>();
            String[] ids = orgIds.split(",");
            for(String id : ids){
                UserOrg orgUser = new UserOrg();
                orgUser.setOrgId(Integer.valueOf(id));
                orgUser.setUserId(userId);
                orgUserList.add(orgUser);
            }
            userOrgMapper.insertList(orgUserList);
        }
    }

    @Override
    public List<String> getDefaultOrg(User user) {
        List<String> orgList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        com.vrv.vap.common.model.User currentUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User cUser = new User();
        BeanUtils.copyProperties(currentUser,cUser);
        List<BaseKoalOrg> koalOrgList = baseKoalOrgService.findByUser(cUser);
        // 当前登录用户管理范围
        List<String> ids = koalOrgList.stream().map(p -> String.valueOf(p.getUuId())).collect(Collectors.toList());
        if (user.getRoleId() != null) {
            String[] roleIds = user.getRoleId().split(",");
            for (String roleId : roleIds) {
                Role role = roleService.findById(Integer.valueOf(roleId));
                if (role.getOrgId() != null) {
                    String[] roleOrgIds = role.getOrgId().split(",");
                    for (String roleOrgId : roleOrgIds) {
                        // 角色管理范围的并集与当前登录用户管理范围取交集
                        if (!orgList.contains(roleOrgId) && ids.contains(roleOrgId)) {
                            orgList.add(roleOrgId);
                        }
                    }
                }
            }
        }
        return orgList;
    }

    @Override
    public void deleteByUserIds(String[] userIds) {
        Example example = new Example(UserOrg.class);
        example.createCriteria().andIn("userId", Arrays.asList(userIds));
        userOrgMapper.deleteByExample(example);
    }
}
