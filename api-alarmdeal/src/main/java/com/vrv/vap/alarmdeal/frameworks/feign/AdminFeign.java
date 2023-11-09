package com.vrv.vap.alarmdeal.frameworks.feign;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.contract.*;
import com.vrv.vap.alarmdeal.business.asset.vo.IpRangeVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.OrgLeaderQuery;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.OrgUserQuery;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.Result;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 权限接口调用
 *
 * @author wd-pc
 */
@FeignClient(name = "api-admin", configuration = ConfigurationFegin.class)
public interface AdminFeign {


    /**
     * 通过当前登陆用户，获得当前登陆用户的信息
     *
     * @param orgUserQuery
     * @return
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VList<User> getUserList(@RequestBody UserQuery userQuery);

    //@ApiOperation(value = "获取所有用户")
    @RequestMapping(value = "/user", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<User>> getAllUser();

    /**
     * 根据RoleCode
     *
     * @param roleCode
     * @return
     */
    @RequestMapping(value = "/user/roleCode/{roleCode}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<User>> getUserByRoleCode(@PathVariable(value = "roleCode") String roleCode); // userbyRoleCode


    /**
     * 根据code获得对应的安全域IP段集合
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/secruity/domain/ip/byCode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<IpRangeVO>> bySecruityCode(@RequestBody IpRangeVO ipRangeVO);

    /**
     * 获取全部地区
     */

    //BaseArea
    @RequestMapping(value = "/area", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseArea>> getAllArea();


    /**
     * 读取系统配置表
     */
    @RequestMapping(value = "/system/config", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<SystemConfig>> allConfig();

    /**
     * 读取系统配置表
     */
    @RequestMapping(value = "/system/config/{confId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<SystemConfig> getConfigById(@PathVariable(value = "confId") String confId);

    @RequestMapping(value = "/secruity/domain", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseSecurityDomain>> getRootDomains();

    @RequestMapping(value = "/secruity/domain/all", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseSecurityDomain>> getAllDomain();

    @RequestMapping(value = "/secruity/domain/single/{code}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<BaseSecurityDomain> getOneDomainByCode(@PathVariable(value = "code") String code);  // getSingleDomain


    @RequestMapping(value = "/secruity/domain/{code}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseSecurityDomain>> getChildrenDomainByCode(@PathVariable(value = "code") String code);


    @RequestMapping(value = "/organization/allSubOrg", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseKoalOrg>> getAllSubOrg();


    @RequestMapping(value = "/organization/byUser/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<BaseKoalOrg> getOrgByUser(@PathVariable(value = "userId") String userId); // byUser

    @RequestMapping(value = "/organization/tree/{code}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseKoalOrg>> getOrgChildren(@PathVariable(value = "code") String code); // getOrgTree


    @RequestMapping(value = "/base/person/zjg", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BasePersonZjg>> getAllPerson();  // getAllBasePersonZjg


    @RequestMapping(value = "/license", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<Licensen> getLicense();

    /**
     * 涉密等级字典信息 ：查询参数（规定值）"parentType": "44a7eb7b-4b01-a53d-d986-f96c131b85e7"
     * @param
     * @return
     */
    /**
     * 涉密等级字典信息 ：查询参数（规定值）"parentType": "44a7eb7b-4b01-a53d-d986-f96c131b85e7"
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/dictionary", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VList<BaseDictAll> getPageDict(@RequestBody Map<String, Object> userQuery);


    /**
     * 发送短信
     */
    @RequestMapping(value = "/shortMessage/byPhoneAndContent", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> shortMessage(@RequestBody Map<String, Object> map);

    @RequestMapping(value = "/organization", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VList<BaseKoalOrg> getOrgPage(@RequestBody Map<String, String> param);

    /**
     * 通过当前登陆用户，获得当前登陆用户的信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<User> getUserById(@PathVariable("userId") int userId);

    /**
     * 通过ID获得对应对应角色
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/role", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<List<Role>> getRoleById(@RequestBody Map<String, Object> param);


    @RequestMapping(value = "/organization/area/byUser/{userId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<List<BaseKoalOrg>> orgByUserId(@PathVariable("userId") String userId);

    /**
     * 通过code获得组织结构
     *
     * @param code
     * @return
     */
    @RequestMapping(value = "organization/{code}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<BaseKoalOrg> orgByCode(@PathVariable("code") String code);

    /**
     * 通过roleId获取role
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "role/{roleId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<Role> getRoleByRoleId(@PathVariable("roleId") String roleId);

    /**
     * 根据ip获得对应区域的组织结构
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/area/areaByIp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<com.vrv.vap.alarmdeal.frameworks.contract.user.BaseArea> getAreaByIp(@RequestBody Map<String, String> map);

    /**
     * 通过IP获得对应的组织结构
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/organization/byIp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResultObjVO<BaseKoalOrg> byIp(@RequestBody Map<String, String> param);


    /**
     * 获得某组织结构下指定角色用户
     *
     * @param orgUserQuery
     * @return
     */
    @RequestMapping(value = "/organization/users", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<User> users(@RequestBody OrgUserQuery orgUserQuery);

    /**
     * 通过当前登陆用户，获得当前登陆用户的组织结构领导
     *
     * @param orgLeaderQuery
     * @return
     */
    @RequestMapping(value = "/organization/members", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<User> members(@RequestBody OrgLeaderQuery orgLeaderQuery);

    /**
     * 通过当前登陆用户上级机构领导上级机构指定成员
     * isLeader：0 指定成员
     * isLeader：1上级机构领导
     *
     * @param orgLeaderQuery
     * @return
     */
    @RequestMapping(value = "/organization/upMembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<User> upMembers(@RequestBody OrgLeaderQuery orgLeaderQuery);

    /**
     * 根据安全域code获得对应的安全域所绑定的人
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/secruity/domain/user/byCode", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<User> byCode(@RequestBody Map<String, Object> map);

    /**
     * 根据登录用户Id获得对应的安全域
     *
     * @return
     */
    @RequestMapping(value = "/secruity/domain/byUser", method = RequestMethod.GET)
    public Result<BaseSecurityDomain> secDomainbyUserId(@RequestHeader("Cookie") String sessionCookie);

    @RequestMapping(value = "/role/getRoleInfo/{code}", method = RequestMethod.GET)
    public ResultObjVO<Role> getRoleInfo(@PathVariable("code") String code);


    @RequestMapping(value = "/organization/rootinfo", method = RequestMethod.GET)
    public VData<BaseKoalOrg> getRootInfo(@RequestHeader("Cookie") String sessionCookie);

    @RequestMapping(value = "/organization/root", method = RequestMethod.GET)
    public VData<BaseKoalOrg> getRoot();

    /**
     * api-common中的下载
     */
    @RequestMapping(value = "/file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadFile(@RequestPart("file") MultipartFile file,
                                          @RequestParam("namespace") String namespace,
                                          @RequestParam("msg") String msg);


    /**
     * 邮件为html格式
     *
     * @param mailVO
     * @return
     */
    @RequestMapping(value = "/sendEmail/sendHtmlEmailToManyDirect", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<Boolean> sendhtmlEmail(@RequestBody MailVO mailVO);

    /**
     * 邮件为纯文本格式
     *
     * @param mailVO
     * @return
     */
    @RequestMapping(value = "/sendEmail/sendSimpleEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<Boolean> sendSimpleEmail(@RequestBody MailVO mailVO);

    /**
     * 审批信息中获取所有用户信息
     * 2023-08
     *
     * @return
     */
    @RequestMapping(value = "/base/person/zjg/getAllUsersAuth", method = RequestMethod.GET)
    public VData<List<Map<String, Object>>> getUserAuth();


    /**
     * 根据CODE 或 IP 查询机构及上级机构
     * @param query
     * @return
     */
    @RequestMapping(value = "/organization/parentOrg", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public VData<BaseKoalOrg> queryOrganizationRelation(@RequestBody OrgRelationQuery query);
}
