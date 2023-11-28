package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.properties.Site;
import com.vrv.vap.admin.common.util.ExcelUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseOrgIpSegment;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.*;


/**
 * 旧版本组织机构，暂时保留，后续删除
 */
@Api(value = "组织机构控制器类")
@RequestMapping(path = "/organization")
@RestController
public class OrganizationController extends ApiController {
    private static final String AREA_CODE = "areaCode";
    private static final String PARENT_CODE = "parentCode";
    private static final String ORG_CODE = "orgCode";
    @Autowired
    UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private BaseOrgIpSegmentService baseOrgIpSegmentService;

    @Autowired
    private Site siteProperties;

    @Autowired
    Cache<String, List<BaseKoalOrgVO>> koalOrgCache;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String CACHE_ORG_KEY = "_BASEINFO:BASE_KOAL_ORG:ALL";

    private final static Logger log = LoggerFactory.getLogger(OrganizationController.class);

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("secretLevel", "{\"0\":\"绝密\",\"1\":\"机密\",\"2\":\"秘密\",\"3\":\"内部\",\"4\":\"非密\"}");
        transferMap.put("secretQualifications", "{\"1\":\"军工一级\",\"2\":\"军工二级\",\"3\":\"非军工\"}");
        transferMap.put("orgType","{\"1\":\"党政机关\",\"2\":\"事业单位\",\"3\":\"国有企业\",\"4\":\"其他单位\"}");
        transferMap.put("protectionLevel","{\"0\":\"绝密\",\"1\":\"机密增强\",\"2\":\"机密一般\",\"3\":\"秘密\"}");
    }

    @GetMapping
    @ApiOperation(value = "获取所有组织机构")
    @SysRequestLog(description = "获取所有组织机构",actionType = ActionType.SELECT)
    public Result getAllOrg() {
        String orgStr = redisTemplate.opsForValue().get(CACHE_ORG_KEY);
        List<BaseKoalOrg> orgList = JSON.parseArray(orgStr,BaseKoalOrg.class);
        if (CollectionUtils.isEmpty(orgList)) {
            orgList = baseKoalOrgService.findAll();
            redisTemplate.opsForValue().set(CACHE_ORG_KEY,JSON.toJSONString(orgList));
        }
        return this.vData(orgList);
    }

    /**
     * 添加组织机构
     */

    @PutMapping
    @ApiOperation(value = "添加组织机构信息")
    @SysRequestLog(description="添加组织机构信息", actionType = ActionType.ADD)
    public Result addOrg(@RequestBody BaseKoalOrg baseKoalOrg) {
        String code = baseKoalOrg.getCode();
        if(StringUtils.isNotEmpty(code)){
            if (!code.matches("^[a-z0-9A-Z]+$")) {
                return this.result(ErrorCode.ORG_CODE_NOT_MATCH);
            }
            List<BaseKoalOrg> baseKoalOrgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class ,"code", baseKoalOrg.getCode());
            if(CollectionUtils.isNotEmpty(baseKoalOrgList)){
                return this.result(ErrorCode.ORG_HAS_CODE);
            }
        } else {
            return this.result(ErrorCode.ORG_CODE_EMPTY);
        }
        // 生成组织机构的层级维护代码
        baseKoalOrgService.generateSubCode(baseKoalOrg);
        if(StringUtils.isEmpty(baseKoalOrg.getSubCode())){
            return this.result(ErrorCode.ORG_SAVE_FAIL_SUBCODE);
        }
        baseKoalOrg.setStatus("0");
        int result = baseKoalOrgService.save(baseKoalOrg);
        if (result == 1) {
            BaseKoalOrg koalOrg = new BaseKoalOrg();
            BeanUtils.copyProperties(baseKoalOrg,koalOrg);
            this.transferParentCode(koalOrg);
            SyslogSenderUtils.sendAddSyslogAndTransferredField(koalOrg,"添加组织机构信息",transferMap);
            koalOrgCache.clear();
            baseKoalOrgService.cacheOrg();
            baseKoalOrgService.sendChangeMessage();
            return this.vData(baseKoalOrg);
        }
        return this.vData(false);
    }

    /**
     * 查询组织机构（分页）
     */
    @PostMapping
    @ApiOperation(value = "查询组织机构（分页）")
    @SysRequestLog(description="查询组织机构", actionType = ActionType.SELECT)
    @ApiParam(required = true, name = "OrganizationQuery", value = "组织结构查询对象")
    public VList<BaseKoalOrg> queryOrg(@RequestBody BaseKoalOrgQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query, BaseKoalOrg.class);
        return this.vList(baseKoalOrgService.findByExample(example));
    }

    /**
     * 修改组织机构
     */
    @PatchMapping
    @ApiOperation(value = "修改组织机构")
    @SysRequestLog(description="修改组织机构", actionType = ActionType.UPDATE)
    public Result updateOrg(@RequestBody BaseKoalOrg baseKoalOrg) {
        BaseKoalOrg baseKoalOrgQuery = new BaseKoalOrg();
        baseKoalOrgQuery.setUuId(baseKoalOrg.getUuId());
        BaseKoalOrg baseKoalOrgSrc = baseKoalOrgService.findOne(baseKoalOrgQuery);
        String code = baseKoalOrg.getCode();
        if (StringUtils.isEmpty(code)) {
            return this.vData(ErrorCode.ORG_CODE_EMPTY);
        }

        if (!code.matches("^[a-z0-9A-Z]+$")) {
            return this.result(ErrorCode.ORG_CODE_NOT_MATCH);
        }

        BaseKoalOrg koalOrg = baseKoalOrgService.findByCode(code);
        if (koalOrg != null && koalOrg.getUuId().intValue() != baseKoalOrg.getUuId().intValue()) {
            return this.vData(ErrorCode.ORG_HAS_CODE);
        }

        int result = baseKoalOrgService.updateSelective(baseKoalOrg);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(baseKoalOrgSrc,baseKoalOrg,"修改组织机构",transferMap);
            koalOrgCache.clear();
            baseKoalOrgService.cacheOrg();
            baseKoalOrgService.sendChangeMessage();
            return this.vData(true);
        }
        return this.vData(false);
    }

    @GetMapping(value = "/ip/byArea")
    @ApiOperation(value = "查询省级下面的所有IP")
    @SysRequestLog(description="查询省级下面的所有IP", actionType = ActionType.SELECT)
    public VData<List<BaseOrgIpSegment>> getAllIp() {
        return this.vData(baseKoalOrgService.getAllProvinceIp());
    }

    /**
     * 查询组织机构下的IP段
     */
    @PostMapping(value = "/ip/byOrgCode")
    @ApiOperation(value = "查询组织机构下的IP段 （分页）")
    @SysRequestLog(description="查询组织机构下的IP段", actionType = ActionType.SELECT)
    public VData<List<IpRangeVO>> queryOrgIps(@RequestBody IpRangeVO ipRangeVO) {
        SyslogSenderUtils.sendSelectSyslog();
        List<BaseOrgIpSegment> baseOrgIpSegments = baseOrgIpSegmentService.findByProperty(BaseOrgIpSegment.class,AREA_CODE,ipRangeVO.getOrgCode());
        List<IpRangeVO> resultList = new ArrayList<>();
        for (BaseOrgIpSegment temp : baseOrgIpSegments) {
            IpRangeVO result = new IpRangeVO();
            result.setOrgCode(ipRangeVO.getOrgCode());
            result.setStartIpValue(temp.getStartIpNum());
            result.setEndIpValue(temp.getEndIpNum());
            resultList.add(result);
        }
        return this.vData(resultList);
    }

    /**
     * 查询组织机构下的IP段 （分页）
     */
    @PostMapping(value = "/ip")
    @ApiOperation(value = "查询组织机构下的IP段 （分页）")
    @SysRequestLog(description="查询组织机构下的IP段", actionType = ActionType.SELECT)
    public VList<BaseOrgIpSegment> queryOrgIp(@RequestBody OrgUserQuery orgUserQuery) {
        if (StringUtils.isNotEmpty(orgUserQuery.getCode())) {
            List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",orgUserQuery.getCode());
            orgUserQuery.setOrgName(baseKoalOrgs.get(0).getName());
        }
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(orgUserQuery,"查询组织机构下的IP段",transferMap);
        Example example = this.pagination(orgUserQuery, BaseOrgIpSegment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(AREA_CODE, orgUserQuery.getCode());
        if(StringUtils.isNotEmpty(orgUserQuery.getStartIp())){
            criteria.andLike("startIpSegment",orgUserQuery.getStartIp());
        }
        if(StringUtils.isNotEmpty(orgUserQuery.getEndIp())){
            criteria.andLike("endIpSegment",orgUserQuery.getEndIp());
        }
        return this.vList(baseOrgIpSegmentService.findByExample(example));
    }

    /**
     * 查询当前用户的组织机构及其下属组织机构
     */
    @GetMapping(value="/allSubOrg")
    @ApiOperation(value = "查询当前用户的组织机构及其下属组织机构")
    @SysRequestLog(description="查询当前用户的组织机构及其下属组织机构", actionType = ActionType.SELECT)
    public VData<List<BaseKoalOrg>> querySubOrg(HttpServletRequest request){
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        String code = loginUser.getOrgCode();
        //获取子机构信息
        List<BaseKoalOrg> baseKoalOrgList =  baseKoalOrgService.findSubOrgByCode(code);
       //查询用户自身机构信息
        List<BaseKoalOrg>  OrgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",code);
        if(CollectionUtils.isNotEmpty(OrgList)){
            BaseKoalOrg baseKoalOrg = OrgList.get(0);
            baseKoalOrgList.add(baseKoalOrg);
        }
        return this.vData(baseKoalOrgList);
    }


    /**
     * 增加组织机构下的IP段
     */
    @PutMapping(value = "/ip")
    @ApiOperation(value = "增加组织机构下的IP段")
    @SysRequestLog(description="增加组织机构下的IP段", actionType = ActionType.ADD)
    public Result addOrgIp(@RequestBody BaseOrgIpSegment baseOrgIpSegment) {
        Long start = IPUtils.ip2int(baseOrgIpSegment.getStartIpSegment());
        Long end = IPUtils.ip2int(baseOrgIpSegment.getEndIpSegment());
        if (start == 0L || end == 0L) {
            return this.result(ErrorCode.ORG_IP_INVALIDATE);
        }
        if (end < start) {
            return this.result(ErrorCode.ORG_IP_RANGE_VALIDATE);
        }
        BaseKoalOrg org = baseKoalOrgService.findByCode(baseOrgIpSegment.getDepartmentCode());
        String parentCode = org.getParentCode();
        Example query;
        if (parentCode.length() > 2 && !org.getCode().equals(siteProperties.getOrgRoot())) {
            query = new Example(BaseOrgIpSegment.class);
            query.createCriteria().andEqualTo(AREA_CODE, parentCode);
            List<BaseOrgIpSegment> parents = baseOrgIpSegmentService.findByExample(query);
            boolean inParent = false;
            for (BaseOrgIpSegment pIp : parents) {
                if (start >= pIp.getStartIpNum() && end <= pIp.getEndIpNum()) {
                    inParent = true;
                    break;
                }
            }
            if (!inParent) {
                return this.result(ErrorCode.ORG_IP_NOT_IN_PARENT);
            }
        }
        query = new Example(BaseOrgIpSegment.class);
        query.createCriteria().andEqualTo(AREA_CODE, baseOrgIpSegment.getDepartmentCode());
        List<BaseOrgIpSegment> selfs = baseOrgIpSegmentService.findByExample(query);
        boolean conflict = false;
        for (BaseOrgIpSegment sIp : selfs) {
            if ((start <= sIp.getEndIpNum() && start >= sIp.getStartIpNum()) || (end <= sIp.getEndIpNum() && end >= sIp.getStartIpNum())) {
                conflict = true;
            }
        }
        if (conflict) {
            return this.result(ErrorCode.ORG_IP_IN_SELF);
        }

        BaseKoalOrg baseKoalOrg = baseKoalOrgService.findByCode(baseOrgIpSegment.getDepartmentCode());
        baseOrgIpSegment.setAreaCode(baseKoalOrg.getCode());
        baseOrgIpSegment.setDepartmentCode("");
        baseOrgIpSegment.setAreaName(baseKoalOrg.getName());
        baseOrgIpSegment.setStartIpNum(start);
        baseOrgIpSegment.setEndIpNum(end);
        int result = baseOrgIpSegmentService.save(baseOrgIpSegment);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(baseOrgIpSegment,"增加组织机构下的IP段");
            koalOrgCache.clear();
            baseKoalOrgService.cacheOrg();
            baseKoalOrgService.sendChangeMessage();
            return this.vData(baseOrgIpSegmentService.findOne(baseOrgIpSegment));
        }
        return this.vData(false);
    }


    /**
     * 删除组织机构下的IP段
     */
    @DeleteMapping(value = "/ip")
    @ApiOperation(value = "删除组织机构下的IP段")
    @SysRequestLog(description="删除组织机构下的IP段", actionType = ActionType.DELETE)
    public Result delOrgIp(@RequestBody DeleteQuery deleteQuery) {
        List<BaseOrgIpSegment> segmentList = baseOrgIpSegmentService.findByids(deleteQuery.getIds());
        int result = baseOrgIpSegmentService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            segmentList.forEach(baseOrgIpSegment -> {
                SyslogSenderUtils.sendDeleteSyslog(baseOrgIpSegment,"删除组织机构下的IP段");
            });
            koalOrgCache.clear();
            baseKoalOrgService.cacheOrg();
            baseKoalOrgService.sendChangeMessage();
            return this.vData(true);
        }
        return this.vData(false);
    }


    /**
     * 批量增加组织机构下的用户
     */
    @PutMapping(value = "/user/{code}")
    @ApiOperation(value = "批量增加组织机构下的用户")
    @SysRequestLog(description="批量增加组织机构下的用户", actionType = ActionType.ADD)
    public Result addOrgUser(@RequestBody List<Integer> userIds, @PathVariable String code) {
        BaseKoalOrg baseKoalOrg = baseKoalOrgService.findByCode(code);
        if (baseKoalOrg == null) {
            return this.result(ErrorCode.ORG_CODE_INVALIDATE);
        }
        for (Integer userId : userIds) {
            User user = userService.findById(userId);
            if (user == null) {
                return this.result(ErrorCode.USER_NOT_EXIST);
            }
            if (StringUtils.isNotBlank(user.getOrgCode())) {
                return new Result("9999", "用户 : (" + user.getName() + ") 已经在机构 : (" + user.getOrgName() + ") 里面了");
            }
        }
        Example example = new Example(User.class);
        example.createCriteria().andIn("id", userIds);
        List<User> users = userService.findByExample(example);
        boolean result = userService.updateOrg(userIds, baseKoalOrg);
        if (result) {
            users.forEach(user -> {
                User userNew = userService.findById(user.getId());
                SyslogSenderUtils.sendUpdateSyslog(user,userNew,"批量修改组织机构下的用户");

            });
        }
        return this.result(result);

    }

    /**
     * 删除组织机构
     */
    @DeleteMapping
    @ApiOperation(value = "删除组织机构")
    @SysRequestLog(description="删除组织机构", actionType = ActionType.DELETE)
    public Result delOrg(@RequestBody DeleteQuery deleteQuery) {
        List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findByProperty(BaseKoalOrg.class, PARENT_CODE, deleteQuery.getIds());
        if (CollectionUtils.isNotEmpty(baseKoalOrgs)) {
            return this.result(ErrorCode.ORG_HAS_SUB);
        }

        List<BaseOrgIpSegment> baseOrgIpSegments = baseOrgIpSegmentService.findByProperty(BaseOrgIpSegment.class, AREA_CODE, deleteQuery.getIds());
        if (CollectionUtils.isNotEmpty(baseOrgIpSegments)) {
            return this.result(ErrorCode.ORG_HAS_IP);
        }

        Example exampleUser = new Example(User.class);
        exampleUser.createCriteria().andEqualTo(ORG_CODE,deleteQuery.getIds()).andNotEqualTo("status",2);
        List<User> users = userService.findByExample(exampleUser);
        if(CollectionUtils.isNotEmpty(users)){
            return this.result(ErrorCode.ORG_HAS_USER);
        }

        List<BaseKoalOrg> orgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",deleteQuery.getIds());
        if(baseKoalOrgService.deleteByOrgIds(deleteQuery.getIds()) >= 1){
            orgList.forEach(baseKoalOrg -> {
                this.transferParentCode(baseKoalOrg);
                SyslogSenderUtils.sendDeleteAndTransferredField(baseKoalOrg,"删除组织机构",transferMap);
            });
            koalOrgCache.clear();
            baseKoalOrgService.cacheOrg();
            baseKoalOrgService.sendChangeMessage();
            return this.result(true);
        }
        return this.result(false);
    }


    /********
     *
     *
     * 说明：下面基本为对外
     *
     *
     * *************/

    /**
     * 查询组织机构根结点
     */
    @GetMapping(value = "/rootinfo")
    @ApiOperation(value = "获取组织机构的根结点", notes = "获取组织机构的根结点：描述")
    @SysRequestLog(description="获取组织机构的根结点", actionType = ActionType.SELECT,manually = false)
    public VData<BaseKoalOrg> getRootInfo(HttpServletRequest request) {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        if(Const.USER_ADMIN.equals(user.getAccount())){
            return this.vData(baseKoalOrgService.findRootBaseKoal());
        }
        return this.vData(baseKoalOrgService.findByCode(user.getOrgCode()));
    }

    /**
     * 查询组织机构根结点
     */
    @GetMapping(value = "/root")
    @ApiOperation(value = "获取组织机构的根结点", notes = "获取组织机构的根结点：描述")
    @SysRequestLog(description="获取组织机构的根结点", actionType = ActionType.SELECT,manually = false)
    public VData<BaseKoalOrg> getRoot() {
        return this.vData(baseKoalOrgService.findRootBaseKoal());
    }


    @PostMapping(value = "/batch")
    @ApiOperation(value = "批量查询组织机构", notes = "批量获取组织机构的名称")
    @SysRequestLog(description="批量查询组织机构", actionType = ActionType.SELECT)
    public VData<Map<String, String>> batchQuery(@RequestBody BatchOrgQuery query) {
        String codes = query.getCodes();
        if (StringUtils.isBlank(codes)) {
            return this.vData(ErrorCode.ORG_CODE_NULL);
        }
        Map<String, String> result = new HashMap<>();
        for (String code : codes.split(",")) {
            BaseKoalOrg koalOrg = baseKoalOrgService.findByCode(code);
            if (koalOrg == null) {
                result.put(code, "");
            } else {
                result.put(code, koalOrg.getName());
            }
        }
        return this.vData(result);
    }

    /**
     * 根据结点ID获取组织机构
     */
    @GetMapping(value = "/tree/{code}")
    @ApiOperation(value = "查询组织机构", notes = "根据组织机构id查询组织")
    @SysRequestLog(description="查询组织机构", actionType = ActionType.SELECT)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "code", value = "组织机构编码", required = true, dataType = "String")})
    public VData<List<BaseKoalOrgVO>> queryOrganizationTree(@PathVariable @ApiParam(value = "机构编码", required = true) String code) {
        List<BaseKoalOrg> koalOrgList = baseKoalOrgService.findByParentCode(code);
        return this.vData(baseKoalOrgService.findHasChildren(koalOrgList));
    }

    /**
     * 根据结点ID获取组织机构
     */
    @GetMapping(value = "/tree/{code}/{businessLine}")
    @ApiOperation(value = "查询指定业务线下的组织机构", notes = "根据组织机构id查询业务线下的组织")
    @SysRequestLog(description="查询指定业务线下的组织机构", actionType = ActionType.SELECT)
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", name = "code", value = "组织机构编码", required = true, dataType = "String"), @ApiImplicitParam(paramType = "query", name = "businessLine", value = "组织机构业务线", required = true, dataType = "String")})
    public VData<List<BaseKoalOrgVO>> queryOrganizationTreeByBussinessLine(@PathVariable @ApiParam(value = "机构编码", required = true) String code, @PathVariable @ApiParam(value = "业务线编号", required = true) String businessLine) {
        Example example = new Example(BaseKoalOrg.class);
        example.setOrderByClause("sort asc");
        example.createCriteria().andEqualTo(PARENT_CODE, code).andEqualTo("businessline", businessLine);
        List<BaseKoalOrg> koalOrgList = baseKoalOrgService.findByExample(example);
        return this.vData(baseKoalOrgService.findHasChildren(koalOrgList));
    }

    /**
     * 根据机构ID查询用户
     */
    @PostMapping(value = "/users")
    @ApiOperation(value = "根据机构ID查询用户")
    @SysRequestLog(description="根据机构ID查询用户", actionType = ActionType.SELECT)
    public VData<List<User>> queryOrganizationUser(@RequestBody OrgUserQuery queryOrg) {
        OrgUserQuery query = new OrgUserQuery();
        BeanUtils.copyProperties(queryOrg,query);
        if (StringUtils.isBlank(query.getCode())) {
            return this.vData(ErrorCode.ORG_CODE_NULL);
        }
        if (StringUtils.isBlank(query.getRoleCode()) && query.getRoleId() <= 0) {
            Example example = new Example(User.class);
            example.setOrderByClause(" id desc ");
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(ORG_CODE, query.getCode());
            if (StringUtils.isNotBlank(query.getUserName())) {
                criteria.andLike("name", "%" + query.getUserName() + "%");
            }
            return this.vData(userService.findByExample(example));
        }

        UserQuery userQuery = new UserQuery();
        userQuery.setOrder_("id");
        userQuery.setBy_("desc");
        userQuery.setOrgCode(query.getCode());

        if (query.getRoleId() > 0) {
            userQuery.setRoleId(query.getRoleId() + "");
        } else {
            Role param = new Role(query.getCode());
            Role role = roleService.findOne(param);
            if (role == null) {
                return this.vData(ErrorCode.ROLE_CODE_EMPTY);
            }
            userQuery.setRoleId(role.getId() + "");
        }
        return this.vData(userService.queryUser(userQuery));
    }

    /**
     * 根据IP查询机构
     */
    @PostMapping(value = "/byIp")
    @ApiOperation(value = "根据IP查询机构")
    @SysRequestLog(description="根据IP查询机构", actionType = ActionType.SELECT, manually = false)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "ip", value = "ip地址", required = true, dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "orgHierarchy", value = "机构级别", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "code", value = "机构编码", dataType = "String"),
    })
    public VData<BaseKoalOrg> queryOrganizationByIp(@RequestBody Map<String, String> param) {
        String ip = param.get("ip");
        if (StringUtils.isEmpty(ip)) {
            return this.vData(ErrorCode.ORG_IP_NULL);
        }
        long ipNum = IPUtils.ip2int(ip);
        if (ipNum == 0) {
            return this.vData(ErrorCode.ORG_IP_INVALIDATE);
        }
        BaseKoalOrg result = baseKoalOrgService.findByIpNum(ipNum, param.get("orgHierarchy"), param.get(ORG_CODE));
        if (result == null) {
            return this.vData(ErrorCode.ORG_IP_NOT_RANGE);
        }
        return this.vData(result);
    }

    /**
     * 包含用户
     */
    @GetMapping(value = "/contented")
    @ApiOperation(value = "查询所有有机构的用户")
    @SysRequestLog(description="查询所有有机构的用户", actionType = ActionType.SELECT, manually = false)
    public VData<List<BaseKoalOrg>> queryOrganizationHasUser() {
        return this.vData(baseKoalOrgService.findAllHasUser());
    }

    /**
     * 根据用户ID查询
     */
    @GetMapping(value = "/byUser/{userId:[\\d]+}")
    @ApiOperation(value = "根据用户ID查询用户所在的机构")
    @SysRequestLog(description="根据用户ID查询用户所在的机构", actionType = ActionType.SELECT)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "String")
    })
    public VData<BaseKoalOrg> queryOrganizationByUser(@PathVariable("userId") @ApiParam(value = "用户id", required = true) Integer userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return this.vData(ErrorCode.ORG_USER_NOT_FIND);
        }
        String orgCode = user.getOrgCode();
        if (StringUtils.isBlank(orgCode)) {
            return this.vData(ErrorCode.ORG_USER_NOT_HAS_ORG);
        }
        return this.vData(baseKoalOrgService.findByCode(user.getOrgCode()));
    }

    /**
     * 根据CODE 或 IP 查询机构及上级机构
     */
    @PostMapping(value = "/relation")
    @ApiOperation(value = "根据CODE 或 IP 查询机构及上级机构")
    @SysRequestLog(description="根据CODE或IP查询机构及上级机构", actionType = ActionType.SELECT)
    public VData<List<BaseKoalOrg>> queryOrganizationRelation(@RequestBody OrgRelationQuery query) {
        List<BaseKoalOrg> orgList = new ArrayList<>();
        String code = query.getCode();
        if (StringUtils.isBlank(code)) {
            String ip = query.getIp();
            if (StringUtils.isBlank(ip)) {
                return this.vData(ErrorCode.ORG_ARGMENT_EMPTY);
            }
            long ipNum = IPUtils.ip2int(ip);
            if (ipNum == 0) {
                return this.vData(ErrorCode.ORG_IP_INVALIDATE);
            }
            BaseKoalOrg org = baseKoalOrgService.findByIpNum(ipNum, null, null);
            if (org != null) {
                orgList.add(org);
                code = org.getParentCode();
            }
        }
        while (!StringUtils.isBlank(code)) {
            BaseKoalOrg org = baseKoalOrgService.findByCode(code);
            if (org == null) {
                break;
            } else {
                orgList.add(org);
                code = org.getParentCode();
            }
        }
        return this.vData(orgList);
    }
    /**
     * 根据CODE 或 IP 查询机构及上级机构
     */
    @PostMapping(value = "/parentOrg")
    @ApiOperation(value = "根据CODE查询上级机构")
    @SysRequestLog(description="根据CODE查询上级机构", actionType = ActionType.SELECT)
    public VData<BaseKoalOrg> queryparentOrg(@RequestBody OrgRelationQuery query) {
        String code = query.getCode();
        if (!StringUtils.isBlank(code)) {
            BaseKoalOrg one = baseKoalOrgService.findByCode(code);
            if (one!=null){
                Example example = new Example(BaseKoalOrg.class);
                example.createCriteria().andEqualTo("code", one.getParentCode());
                List<BaseKoalOrg> byExample = baseKoalOrgService.findByExample(example);
                if (byExample.size()>0){
                    return this.vData(byExample.get(0));
                }
            }
        }
        return this.vData(ErrorCode.ORG_UP_NOT_FIND);
    }
    /**
     * 根据Code查询机构
     */
    @GetMapping(value = "/{code}")
    @ApiOperation(value = "根据code查询机构", notes = "")
    @SysRequestLog(description="根据code查询机构", actionType = ActionType.SELECT)
    public VData<BaseKoalOrg> getOrganization(@PathVariable("code") @ApiParam(value = "机构编码", required = true) String code) {
        return this.vData(baseKoalOrgService.findByCode(code));
    }

    /**
     * 查询某机构的成员
     * code 必传
     * 可传入参数：　isLeader: 0 = 不是领导 ，1 = 是领导
     */
    @PostMapping(value = "/members")
    @ApiOperation("查询机构的成员")
    @SysRequestLog(description="查询机构的成员", actionType = ActionType.SELECT)
    public VData<List<User>> queryMembers(@RequestBody OrgLeaderQuery param) {
        String code = param.getCode();
        if (StringUtils.isBlank(code)) {
            return this.vData(ErrorCode.ORG_CODE_NULL);
        }
        Example example = new Example(User.class);
        example.setOrderByClause(" id desc ");
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(ORG_CODE, code);
        if (param.getCode() != null) {
            criteria.andEqualTo("isLeader", param.getIsLeader().byteValue());
        }
        return this.vData(userService.findByExample(example));
    }

    /**
     * 查询用户的上级机构
     * 可传入参数：　isLeader: 0 = 不是领导 ，1 = 是领导
     */
    @PostMapping(value = "/upOrg")
    @ApiOperation("查询上级机构")
    @SysRequestLog(description="查询上级机构", actionType = ActionType.SELECT)
    public VData<BaseKoalOrg> queryUpOrg(@RequestBody OrgLeaderQuery param) {
        String code = param.getCode();
        if (StringUtils.isBlank(code)) {
            if (StringUtils.isBlank(param.getUserId())) {
                return this.vData(ErrorCode.ORG_CONDITION_EMPTY);
            }
            User user = userService.findById(Integer.parseInt(param.getUserId()));
            if (user == null) {
                return this.vData(ErrorCode.ORG_USER_NOT_HAS_ORG);
            }
            code = user.getOrgCode();
        }
        BaseKoalOrg org = baseKoalOrgService.findByCode(code);
        if (org != null) {
            BaseKoalOrg parent = baseKoalOrgService.findByCode(org.getParentCode());
            if (parent == null) {
                return this.vData(ErrorCode.ORG_IS_SUPPER);
            }
            return this.vData(parent);
        }
        return this.vData(ErrorCode.ORG_CAN_NOT_FIND);
    }

    /**
     * 查询用户的上级成员
     * 可传入参数：　isLeader: 0 = 不是领导 ，1 = 是领导
     *
     */
    @PostMapping(value = "/upMembers")
    @ApiOperation("查询上级机构的成员")
    @SysRequestLog(description="查询上级机构的成员", actionType = ActionType.SELECT)
    public VData<List<User>> queryUpMembers(@RequestBody OrgLeaderQuery param) {
        String code = param.getCode();
        if (StringUtils.isBlank(code)) {
            if (StringUtils.isBlank(param.getUserId())) {
                return this.vData(ErrorCode.ORG_CONDITION_EMPTY);
            }
            User user = userService.findById(Integer.parseInt(param.getUserId()));
            if (user == null) {
                return this.vData(ErrorCode.ORG_USER_NOT_HAS_ORG);
            }
            if (StringUtils.isBlank(user.getOrgCode())) {
                return this.vData(ErrorCode.ORG_USER_NOT_HAS_ORG);
            }
            code = user.getOrgCode();
        }
        BaseKoalOrg baseKoalOrg = baseKoalOrgService.findByCode(code);
        if (baseKoalOrg == null) {
            return this.vData(ErrorCode.ORG_CODE_INVALIDATE);
        }
        param.setCode(baseKoalOrg.getParentCode());
        param.setUserId(null);
        return this.queryMembers(param);
    }

    /**
     * 根据ip段查询组织机构 （分页）
     */
    @PostMapping(value = "/byIprange")
    @ApiOperation(value = "根据ip段查询组织机构 （分页）")
    @SysRequestLog(description="根据ip段查询组织机构（分页）", actionType = ActionType.SELECT)
    public VList<BaseKoalOrg> queryOrgByIprange(@RequestBody IpRangeQuery ipRangeQuery) {
        return this.vList(baseKoalOrgService.getOrgPageByIpRange(ipRangeQuery));
    }

    /**
     * 导入组织机构信息校验
     */
    @GetMapping(value = "/import")
    @ApiOperation(value = "导入组织机构信息校验")
    @SysRequestLog(description="导入组织机构信息校验", actionType = ActionType.AUTO)
    public Result importOrg(@RequestParam(value = "id") String id, @RequestParam(value = "importType")Integer importType) {
        return this.vData(baseKoalOrgService.validateImportOrg(id,importType));
    }

    @PostMapping(value = "/import/save")
    @ApiOperation("保存导入的组织机构")
    @SysRequestLog(description="保存导入的组织机构", actionType = ActionType.IMPORT,manually = false)
    public Result saveImportOrg(@RequestBody OrgImportSaveVO orgImportSaveVO) {
        List<BaseKoalOrgExcel> baseKoalOrgExcelList = orgImportSaveVO.getBaseKoalOrgExcelList();
        Integer importType = orgImportSaveVO.getImportType();
        long rootCount = baseKoalOrgExcelList.stream().filter(item -> "单位".equals(item.getType())).count();
        if (rootCount == 0 && importType == 0) {
            return this.vData(ErrorCode.ORGANZATION_TYPE_ORG);
        }
        baseKoalOrgService.importOrg(baseKoalOrgExcelList,importType);
        baseKoalOrgService.cacheOrg();
        baseKoalOrgService.sendChangeMessage();
        return this.vData(Global.OK);
    }

    /**
     * 导出组织机构
     */
    @GetMapping(value = "/export")
    @ApiOperation(value = "导出组织机构")
    @SysRequestLog(description="导出组织机构", actionType = ActionType.EXPORT)
    public Result export(HttpServletResponse httpServletResponse) throws FileNotFoundException {
        SyslogSenderUtils.sendExportSyslog();
        baseDictAllService.generateDicMap();
        Map<String, Map<String,String>> dicMap = baseDictAllService.getDicCodeToValueMap();

        List<BaseKoalOrg> baseKoalOrgs = baseKoalOrgService.findAll();
        List<BaseOrgIpSegment> baseOrgIpSegments = baseOrgIpSegmentService.findAll();
        List<BaseKoalOrgExcel> baseKoalOrgExcelList = new ArrayList<>();
        baseKoalOrgs.forEach(p -> {
            BaseKoalOrgExcel baseKoalOrgExcel = new BaseKoalOrgExcel();
            BeanUtils.copyProperties(p, baseKoalOrgExcel);
            baseKoalOrgExcel.setSecretLevel(dicMap.get("zjg-保密等级").get(String.valueOf(p.getSecretLevel())));
            baseKoalOrgExcel.setType(dicMap.get("zjg-机构类型").get(p.getType()));
            baseKoalOrgExcel.setProtectionLevel(dicMap.get("网络信息-防护等级").get(String.valueOf(p.getProtectionLevel())));
            baseKoalOrgExcel.setSecretQualifications(dicMap.get("zjg-保密资格").get(String.valueOf(p.getSecretQualifications())));
            baseKoalOrgExcel.setOrgType(dicMap.get("zjg-单位类别").get(String.valueOf(p.getOrgType())));
            baseKoalOrgExcelList.add(baseKoalOrgExcel);
            StringBuilder ipRanges = new StringBuilder();
//            baseOrgIpSegments.stream().filter(i -> p.getCode().equals(i.getAreaCode())).forEach(i -> {
//                ipRanges.append(i.getStartIpSegment()).append("-").append(i.getEndIpSegment()).append(",");
//            });
//            if (ipRanges.length() > 0) {
//                ipRanges.deleteCharAt(ipRanges.length() - 1);
//                baseKoalOrgExcel.setIpRanges(ipRanges.toString());
//            }
        });

        ExcelUtil.exportExcel(baseKoalOrgExcelList, null, "组织机构信息", BaseKoalOrgExcel.class, "组织机构信息.xls",true, httpServletResponse);

        return null;
    }

    @ApiOperation("父账号管理范围组织机构树")
    @GetMapping(value = "/tree/user")
    @SysRequestLog(description="父账号管理范围组织机构树", actionType = ActionType.SELECT)
    public VData getOrganizationUserTree() {
        //根据用户获取拥有的安全域权限
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //判断用户是否为admin用户
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        if (Const.USER_ADMIN.equals(user.getAccount()) || Const.SEC_ADMIN.equals(user.getAccount())) {
            List<BaseKoalOrg> allOrg = baseKoalOrgService.findAll();
            List<BaseKoalOrgVO> treeList = baseKoalOrgService.generateKoalOrgVO(allOrg);
            treeList.stream().forEach(p -> p.setIsAuthorized(1));
            return this.vData(treeList);
        }
        Set<String> orgCodeSet = (Set<String>) request.getSession().getAttribute("_ORG");
        List<BaseKoalOrgVO> treeList = baseKoalOrgService.getTreeList(orgCodeSet);
        return this.vData(treeList);
    }

    @ApiOperation("根据用户id获取管理范围")
    @GetMapping(value = "/area/byUser/{userId:[\\d]+}")
    @SysRequestLog(description="根据用户id获取管理范围", actionType = ActionType.SELECT)
    public VData getOrganizationAreaByUser(@PathVariable("userId") Integer userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return this.vData(ErrorCode.ORG_USER_NOT_FIND);
        }
        List<BaseKoalOrg> orgList = baseKoalOrgService.findByUser(user);
        return this.vData(orgList);
    }

    /**
     * 根据id查询机构
     */
    @GetMapping(value = "/area/{id}")
    @ApiOperation(value = "根据Id查询机构")
    @SysRequestLog(description="根据Id查询机构", actionType = ActionType.SELECT)
    public VData<BaseKoalOrg> getOrganizationById(@PathVariable("id") Integer id) {
        return this.vData(baseKoalOrgService.findById(id));
    }

    @ApiOperation("下载模板")
    @GetMapping(path = "/download/{fileType}")
    @SysRequestLog(description="下载模板", actionType = ActionType.DOWNLOAD)
    public void downloadFile(@PathVariable("fileType")@ApiParam(value = "文件类型(org:组织机构,person:人员)") String fileType, HttpServletResponse response) {
        String fileName = "";
        if (fileType.equals("org")) {
            fileName = "组织机构信息导入模板.xls";
        }
        if (fileType.equals("person")) {
            fileName = "人员信息导入模板.xls";
        }
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.DOWNLOAD, "下载模板：【文件名：" + fileName + "】", null, "1");

        ClassPathResource classPathResource = new ClassPathResource(Paths.get("/templates", fileName).toString());
        try (InputStream fis = classPathResource.getInputStream();
             ServletOutputStream out = response.getOutputStream();
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/binary;charset=utf-8");
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error("下载失败",  e);
        }
    }

    private void transferParentCode(BaseKoalOrg baseKoalOrg) {
        List<BaseKoalOrg> orgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",baseKoalOrg.getParentCode());
        if (CollectionUtils.isNotEmpty(orgList)) {
            BaseKoalOrg org = orgList.get(0);
            baseKoalOrg.setParentCode(org.getName());
        }
    }
}
