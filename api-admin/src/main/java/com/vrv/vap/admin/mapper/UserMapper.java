package com.vrv.vap.admin.mapper;

import java.util.List;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.admin.vo.RolePersonQuery;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.UserQuery;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    List<User> queryUser(@Param("param") UserQuery query ,@Param("roleids") List<String> roleids,@Param("orgSubs") List<String> orgSubs);

    int updateOrg(@Param("ids") List<Integer> userIds, @Param("org") BaseKoalOrg baseKoalOrg);

    int updateArea(@Param("ids") List<Integer> userIds, @Param("area") BaseArea baseArea);

    List<User> queryByCtyId(@Param("ctyId") Integer ctyId);

    List<User> getUserNotBuildIn(@Param("monitorRoleCode") String monitorRoleCode);

    Page<User> queryUserByRoleOrPerson(@Param("param") RolePersonQuery query);

}