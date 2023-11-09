package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.UserDomainMapper;
import com.vrv.vap.admin.model.UserDomain;
import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.admin.service.UserDomainService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserDomainServiceImpl extends BaseServiceImpl<UserDomain> implements UserDomainService {
    @Resource
    private UserDomainMapper userDomainMapper;

    @Override
    public void deleteByUserIds(String[] userIds) {
        Example example = new Example(UserRole.class);
        example.createCriteria().andIn("userId", Arrays.asList(userIds));
        userDomainMapper.deleteByExample(example);
    }

    @Override
    public void saveUserDomains(String domainCode, Integer userId) {
        if(StringUtils.isNotEmpty(domainCode)){
            String[] domainCodes = domainCode.split(",");
            List<UserDomain> userDomains = new ArrayList<>();
            for(String domainStr : domainCodes){
                UserDomain ud = new UserDomain();
                ud.setDomainCode(domainStr);
                ud.setUserId(userId);
                userDomains.add(ud);
            }
            userDomainMapper.insertList(userDomains);
        }
    }

    @Override
    public void deleteAllUserDomain() {
        List<UserDomain> userDomainList = userDomainMapper.selectAll();
        if (CollectionUtils.isNotEmpty(userDomainList)) {
            for (UserDomain userDomain : userDomainList) {
                userDomainMapper.deleteByPrimaryKey(userDomain.getId());
            }
        }
    }
}
