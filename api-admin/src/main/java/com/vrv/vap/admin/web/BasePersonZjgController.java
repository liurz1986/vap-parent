package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.ExcelUtil;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BasePersonZjgService;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.admin.vo.BasePersonZjgExcel;
import com.vrv.vap.admin.vo.BasePersonZjgQuery;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
* @BelongsPackage com.vrv.vap.admin.web
* @Author CodeGenerator
* @CreateTime 2021/08/09
* @Description (BasePersonZjg相关接口)
* @Version
*/
@RestController
@Api(value = "人员基础信息")
@RequestMapping("/base/person/zjg")
public class BasePersonZjgController extends ApiController {

    private static final Logger log = LoggerFactory.getLogger(BasePersonZjgController.class);

    @Autowired
    private BasePersonZjgService basePersonZjgService;


    @Autowired
    private FileUploadInfoService fileUploadInfoService;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String CACHE_PERSON_ZJG_KEY = "_BASEINFO:BASE_PERSON_ZJG:ALL";

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("secretLevel", "{\"4\":\"核心\",\"3\":\"重要\",\"2\":\"一般\",\"1\":\"非密\"}");
        transferMap.put("personType","{\"1\":\"内部运维人员\",\"2\":\"服务外包运维人员\",\"3\":\"内部业务人员\",\"4\":\"外部驻场业务人员\",\"5\":\"其它服务厂商\"}");
    }

    /**
    * 获取所有数据--BasePersonZjg
    */
    @ApiOperation(value = "获取所有人员信息")
    @GetMapping
    public VData< List<BasePersonZjg>> getAllBasePersonZjg() {
        String personStr = redisTemplate.opsForValue().get(CACHE_PERSON_ZJG_KEY);
        List<BasePersonZjg> list = JSON.parseArray(personStr,BasePersonZjg.class);
        if (CollectionUtils.isEmpty(list)) {
            list = basePersonZjgService.findAll();
            redisTemplate.opsForValue().set(CACHE_PERSON_ZJG_KEY, JSON.toJSONString(list));
        }
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加人员信息")
    @PutMapping
    @SysRequestLog(description="添加人员信息", actionType = ActionType.ADD)
    public VData addBasePersonZjg(@RequestBody BasePersonZjg basePersonZjg) {
        String userNo = basePersonZjg.getUserNo();
        if (StringUtils.isEmpty(userNo)) {
            return this.vData(ErrorCode.PERSON_CODE_NULL);
        }
        if (!userNo.matches("^[a-z0-9A-Z@#$%^&*()_+\\-={}|\\:;:\",<.>/?`~]+$")) {
            return this.vData(ErrorCode.PERSON_NO_INVALID);
        }

        List<BasePersonZjg> basePersonZjgs = basePersonZjgService.findByProperty(BasePersonZjg.class,"userNo",basePersonZjg.getUserNo());
        if (CollectionUtils.isNotEmpty(basePersonZjgs)) {
            return this.vData(ErrorCode.PERSON_EXIST);
        }

        basePersonZjg.setCreateTime(new Date());
        basePersonZjg.setUserIdnEx(basePersonZjg.getUserIdnEx()==null?"":basePersonZjg.getUserIdnEx());
        int result = basePersonZjgService.save(basePersonZjg);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(basePersonZjg,"添加人员信息",transferMap);
        }
        basePersonZjgService.cachePerson();
        basePersonZjgService.sendChangeMessage();
        BasePersonZjg basePersonQuery = new BasePersonZjg();
        basePersonQuery.setUserNo(basePersonZjg.getUserNo());
        return this.vData(basePersonZjgService.findOne(basePersonQuery));
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改人员信息")
    @PatchMapping
    @SysRequestLog(description="修改人员信息", actionType = ActionType.UPDATE)
    public Result updateBasePersonZjg(@RequestBody BasePersonZjg  basePersonZjg) {
        BasePersonZjg basePersonZjgSec = basePersonZjgService.findById(basePersonZjg.getId());
        basePersonZjg.setUserIdnEx(basePersonZjg.getUserIdnEx()==null?"":basePersonZjg.getUserIdnEx());
        int result = basePersonZjgService.update(basePersonZjg);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(basePersonZjgSec,basePersonZjg,"修改人员信息",transferMap);
        }
        basePersonZjgService.cachePerson();
        basePersonZjgService.sendChangeMessage();
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除人员信息")
    @DeleteMapping
    @SysRequestLog(description="删除人员信息", actionType = ActionType.DELETE)
    public Result delBasePersonZjg(@RequestBody DeleteQuery deleteQuery) {
        List<BasePersonZjg> personZjgList = basePersonZjgService.findByids(deleteQuery.getIds());
        int result = basePersonZjgService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            personZjgList.forEach(basePersonZjg -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(basePersonZjg,"删除人员信息",transferMap);
            });
        }
        basePersonZjgService.cachePerson();
        basePersonZjgService.sendChangeMessage();
        return this.result(result > 0);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询人员信息（分页）")
    @PostMapping
    @SysRequestLog(description="查询人员信息", actionType = ActionType.SELECT)
    public VList<BasePersonZjg> queryBasePersonZjg(@RequestBody BasePersonZjgQuery basePersonZjgQuery) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(basePersonZjgQuery,"查询人员信息",transferMap);
        String orgCode = basePersonZjgQuery.getOrgCode();
        List<String> userType = basePersonZjgQuery.getUserType();
        List<String> codeList = new ArrayList<>();
        if (StringUtils.isNotEmpty(orgCode)) {
            getChildOrgList(orgCode, codeList);
        }

        basePersonZjgQuery.setOrgCode(null);
        basePersonZjgQuery.setUserType(null);
        Example example = this.pageQuery(basePersonZjgQuery, BasePersonZjg.class);
        if (StringUtils.isNotEmpty(orgCode) && CollectionUtils.isNotEmpty(example.getOredCriteria())) {
            example.getOredCriteria().get(0).andIn("orgCode", codeList);
        }

        if (CollectionUtils.isNotEmpty(userType)) {
            example.getOredCriteria().get(0).andIn("personType", userType);
        }
        //Example example = this.pageQuery(basePersonZjgQuery, BasePersonZjg.class);
        List<BasePersonZjg> list =  basePersonZjgService.findByExample(example);
        return this.vList(list);
    }


    @ApiOperation(value = "查询最近12个月人员变化趋势")
    @PostMapping("/trend")
    @SysRequestLog(description="查询最近12个月人员变化趋势", actionType = ActionType.SELECT)
    public Result queryBasePersonZjgTrend(@RequestBody BasePersonZjgQuery basePersonZjgQueryOrg) {
        BasePersonZjgQuery basePersonZjgQuery = new BasePersonZjgQuery();
        BeanUtils.copyProperties(basePersonZjgQueryOrg,basePersonZjgQuery);
        List<Map> mapList = basePersonZjgService.queryBasePersonTrend(basePersonZjgQuery);
        if (CollectionUtils.isNotEmpty(mapList)) {
            return this.vList(mapList,mapList.size());
        }
        return this.result(false);
    }


    /**
     * 导入人员信息校验
     */

    @GetMapping(value = "/import/{id}")
    @ApiOperation(value = "导入人员信息校验")
    @SysRequestLog(description="导入人员信息校验", actionType = ActionType.AUTO)
    public Result importPerson(@PathVariable String id) {
        return this.vData(basePersonZjgService.validateImportPerson(id));
    }

    @PostMapping(value = "/import/save")
    @ApiOperation(value = "保存导入人员信息")
    @SysRequestLog(description="保存导入人员信息", actionType = ActionType.IMPORT,manually = false)
    public Result saveImportPerson(@RequestBody List<BasePersonZjgExcel> basePersonZjgExcelList) {
        basePersonZjgService.importOrg(basePersonZjgExcelList);
        basePersonZjgService.cachePerson();
        basePersonZjgService.sendChangeMessage();
        return this.vData(Global.OK);
    }


    /**
     * 导出人员信息
     */

    @GetMapping(value = "/export")
    @ApiOperation(value = "导出人员信息")
    @SysRequestLog(description="导出人员信息", actionType = ActionType.EXPORT)
    public Result export(HttpServletResponse httpServletResponse,HttpServletRequest request) throws Exception {
        SyslogSenderUtils.sendExportSyslog();
        String userName = request.getParameter("userName");
        String personRank = request.getParameter("personRank");
        String secretLevel = request.getParameter("secretLevel");
        String personType = request.getParameter("personType");
        String orgCode = request.getParameter("orgCode");
        Example example = new Example(BasePersonZjg.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(userName)) {
            criteria.andLike("userName","%" + userName + "%");
        }
        if (StringUtils.isNotEmpty(personRank)) {
            criteria.andLike("personRank","%" + personRank + "%");
        }
        if (StringUtils.isNotEmpty(orgCode)) {
            List<String> codeList = new ArrayList<>();
            getChildOrgList(orgCode, codeList);
            criteria.andIn("orgCode",codeList);
        }
        if (StringUtils.isNotEmpty(secretLevel)) {
            criteria.andEqualTo("secretLevel",secretLevel);
        }
        if (StringUtils.isNotEmpty(personType)) {
            criteria.andEqualTo("personType",personType);
        }

        List<BasePersonZjg> basePersonZjgs = basePersonZjgService.findByExample(example);

        baseDictAllService.generateDicMap();
        Map<String, Map<String,String>> dicMap = baseDictAllService.getDicCodeToValueMap();

        List<BasePersonZjgExcel> basePersonZjgExcels = new ArrayList<>();
        basePersonZjgs.forEach(p->{
            BasePersonZjgExcel basePersonZjgExcel = new BasePersonZjgExcel();
            BeanUtils.copyProperties(p,basePersonZjgExcel);
            basePersonZjgExcel.setPersonType(dicMap.get("人员信息-人员类型").get(p.getPersonType()));
            basePersonZjgExcel.setSecretLevel(dicMap.get("人员信息-人员密级/SM等级").get(String.valueOf(p.getSecretLevel())));
            basePersonZjgExcels.add(basePersonZjgExcel);
        });
        ExcelUtil.exportExcel(basePersonZjgExcels, null, "人员信息", BasePersonZjgExcel.class, "人员信息.xls",true, httpServletResponse);

        return null;
    }

    private void getChildOrgList(String orgCode, List<String> codeList) {
        codeList.add(orgCode);
        List<BaseKoalOrg> childList = baseKoalOrgService.findByParentCode(orgCode);
        if (CollectionUtils.isNotEmpty(childList)) {
            for (BaseKoalOrg baseKoalOrg : childList) {
                getChildOrgList(baseKoalOrg.getCode(), codeList);
            }
        }
    }

    @GetMapping(path = "/countByType")
    @ApiOperation("获取普通用户及管理员数量")
    @SysRequestLog(description = "获取普通用户及管理员数量", actionType = ActionType.SELECT)
    public VData getPersonCountByType() {
        Map map = new HashMap();
        List<BasePersonZjg> list = basePersonZjgService.findAll();
        if (CollectionUtils.isNotEmpty(list)) {
            List<BasePersonZjg> commonList = list.stream().filter(p -> "3".equals(p.getPersonType()) || "4".equals(p.getPersonType())).collect(Collectors.toList());
            List<BasePersonZjg> adminList = list.stream().filter(p -> "1".equals(p.getPersonType()) || "2".equals(p.getPersonType())).collect(Collectors.toList());
            map.put("commonCount",commonList.size());
            map.put("adminCount",adminList.size());
        } else {
            map.put("commonCount",0);
            map.put("adminCount",0);
        }
        return this.vData(map);
    }

    @GetMapping(value = "/userNo/{userNo}")
    @ApiOperation("根据人员编号获取人员信息")
    public Result getPersonByUserNo(@PathVariable String userNo) {
        if (StringUtils.isNotEmpty(userNo)) {
            List<BasePersonZjg> list = basePersonZjgService.findByProperty(BasePersonZjg.class,"userNo",userNo);
            if (CollectionUtils.isNotEmpty(list)) {
                return this.vData(list.get(0));
            }
        }
        return this.vData(false);
    }

    /**
     * 获取所有数据--审批类型功能
     * 2023-08
     */
    @ApiOperation(value = "获取所有人员信息(审批类型功能)")
    @GetMapping(value = "/getAllUsersAuth")
    public Result getAllUsersAuth() {
        return this.vData(basePersonZjgService.getAllUsersAuth());
    }
}