package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.RolePersonQuery;
import com.vrv.vap.admin.vo.UserQuery;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
public interface UserService extends BaseService<User> {

    List<User> queryUser(UserQuery query);

    boolean updateOrg(List<Integer> userIds, BaseKoalOrg baseKoalOrg);

    boolean updateArea(List<Integer> userIds, BaseArea baseArea);

    User findOneUser(User user);

    Integer deleteUserByIds(String ids);

    List<User> getUserNotBuildIn(String monitorRoleCode);

    /**
     * 获取楚天云机构信息
     * */
    boolean getCtyOrganizations(String token,String url);


    /**
     *获取楚天云用戶信息
     */
    boolean getCtyUserInfo(String token,String url);


    List<User> queryByCtyId(Integer ctyId);

    Integer updateUserRole(User user);

    Integer updateUserDomain(User user);

    Integer updateUserOrg(User user);

    int  insertUserList(List<User> userList);

    void deleteAllUser();

    /**
     * 信创获取token信息
     * @return
     */
    String getXcToken();

    /**
     * 校验信创token信息
     * @return
     */
    String validateXcToken(String token);

    String validationToken(String token,String appName,String appId);

    List<User> queryUserByRoleOrPerson(RolePersonQuery query);

    List<User> getBusinessAndOperationUser(String roleId,String dealType);
}
