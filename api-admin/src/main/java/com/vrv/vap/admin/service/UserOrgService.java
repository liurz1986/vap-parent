package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.model.UserOrg;
import com.vrv.vap.base.BaseService;

import java.util.List;

public interface UserOrgService extends BaseService<UserOrg> {

   void saveOrgUsers(String orgIds,Integer userId);

   void deleteByUserIds(String[] userIds);

   /**
    * 添加用户时获取用户默认组织机构
    * @param user
    * @return
    */
   List<String> getDefaultOrg(User user);
}
