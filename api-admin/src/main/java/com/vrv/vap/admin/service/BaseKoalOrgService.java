package com.vrv.vap.admin.service;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseOrgIpSegment;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by CodeGenerator on 2018/09/13.
 */
public interface BaseKoalOrgService extends BaseService<BaseKoalOrg> {

   List<User> queryUsers(UserQuery userQuery);

   long queryUsersCount(UserQuery userQuery);

  List<UserKeyVo> queryUsersKey(UserQuery userQuery);

  long queryUsersKeyCount(UserQuery userQuery);

    BaseKoalOrg findByCode(String code);

    List<BaseKoalOrg> findByParentCode(String parentCode);

    List<BaseKoalOrgVO> findHasChildren(List<BaseKoalOrg> koalOrgList);

    BaseKoalOrg findByIpNum(long ipNum, String orgHierarchy, String orgCode);
//
//    List<OrgMember> queryOrgUsers(OrgUserQuery query);
//
//    List<OrgMember> queryMembers(OrgLeaderQuery param);
//
//    List<OrgMember> queryUpMembers(OrgLeaderQuery param);

    Integer deleteByOrgIds(String ids);

    List<BaseKoalOrg> findAllHasUser();

    Page<BaseKoalOrg> getOrgPageByIpRange(IpRangeQuery iprange);

    List<BaseOrgIpSegment> getAllProvinceIp();

    BaseKoalOrg generateSubCode(BaseKoalOrg baseKoalOrg);

    void  initSubCode();

    List<BaseKoalOrg> findSubOrgByCode(String code);

    void deleteAllOrg();

    BaseKoalOrg findRootBaseKoal();

  void importOrg(List<BaseKoalOrgExcel> baseKoalOrgExcelList,Integer importType);

  List<BaseKoalOrgVO> generateKoalOrgVO(List<BaseKoalOrg> koalOrgList);

  BaseKoalOrgVO findChildren(BaseKoalOrgVO tree, List<BaseKoalOrgVO> list);

  List<BaseKoalOrgVO> getTreeList(Set<String> orgCodeSet);

  String sync();

  List<BaseKoalOrg> findByUser(User user);

  void cacheOrg();

  Map<String,Object> validateImportOrg(String id,Integer importType);

  void sendChangeMessage();
}
