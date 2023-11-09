package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseOrgIpSegment;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.BaseKoalOrgVO;
import com.vrv.vap.admin.vo.UserKeyVo;
import com.vrv.vap.admin.vo.UserQuery;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface BaseKoalOrgMapper extends BaseMapper<BaseKoalOrg> {
    BaseKoalOrg findByIpNum(@Param("ipNum") long ipNum,@Param("orgHierarchy")  String orgHierarchy,@Param("code")  String code);

    List<BaseKoalOrgVO> findAll();

//    List<OrgMember> queryOrgUsers(@Param("param") OrgUserQuery query);

//    List<OrgMember> queryMembers(@Param("param") OrgLeaderQuery param);

//    List<OrgMember> queryUpMembers(@Param("param") OrgLeaderQuery param);

    List<BaseKoalOrg> findAllHasUser();

    List<BaseOrgIpSegment> getAllProvinceIp();

    List<User> queryUsers(@Param("userQuery")UserQuery userQuery);

    long queryUsersCount(@Param("userQuery")UserQuery userQuery);

    List<UserKeyVo> queryUsersKey(@Param("userQuery")UserQuery userQuery);

    long queryUsersKeyCount(@Param("userQuery")UserQuery userQuery);
}