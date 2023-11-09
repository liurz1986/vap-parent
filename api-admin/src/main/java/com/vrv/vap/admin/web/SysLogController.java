package com.vrv.vap.admin.web;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.common.condition.VapOrGwCondition;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.EventTypeEnum;
import com.vrv.vap.admin.common.enums.LoginTypeEnum;
import com.vrv.vap.admin.common.enums.RoleEnum;
import com.vrv.vap.admin.common.enums.TypeEnum;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.ExportExcelUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.ResourceUtil;
import com.vrv.vap.admin.model.ResultBody;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.model.SysLog;
import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.admin.service.RoleService;
import com.vrv.vap.admin.service.SysLogService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.UserRoleService;
import com.vrv.vap.admin.vo.ListSysLogQuery;
import com.vrv.vap.admin.vo.LoginThirtyDayVO;
import com.vrv.vap.admin.vo.SysLogQuery;
import com.vrv.vap.admin.vo.SysRequestLogVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.Query;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.model.SystemLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.web
 * @Author tongliang@VRV
 * @CreateTime 2019/04/08 14:51
 * @Description (系统日志功能相关接口)
 * @Version
 */
@RestController
@Api(value = "三权分立权限界定系统日志")
@RequestMapping("/syslog")
@Conditional(VapOrGwCondition.class)
public class SysLogController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(SysLogController.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    private static final String REQUEST_METHOD_GET = "GET";

    private static final String REQUEST_METHOD_POST = "POST";

    private static final Integer RESULT_SUCCESS = 1;

    private static Map<String, Object> transferMap = new HashMap<>();

    private static final String COMMON = "common";

    private static final String UNCOMMON = "unCommon";

    static {
        transferMap.put("type", "{\"0\":\"登录\",\"1\":\"查询\",\"2\":\"新增\",\"3\":\"修改\",\"4\":\"删除\",\"5\":\"退出\",\"6\":\"浏览\",\"7\":\"导出\",\"8\":\"导入\",\"9\":\"下载\",\"10\":\"上传\"}");
        transferMap.put("loginType", "{\"0\":\"普通登录\",\"1\":\"证书登录\",\"2\":\"虹膜登录\"}");
        transferMap.put("responseResult", "{\"1\":\"成功\",\"0\":\"失败\"}");
    }

    /**
     * @Description (获取审计日志信息)
     */
    @PostMapping("/separationSyslog")
    @ApiOperation(value = "获取审计日志信息")
    @SysRequestLog(description="查询审计日志信息", actionType = ActionType.SELECT)
    public Result separationSyslog(HttpServletRequest request,@RequestBody ListSysLogQuery listSysLogQuery) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(listSysLogQuery,"查询审计日志信息",transferMap);
        Short confEnable = systemConfigService.getThreePowerEnable();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
            listSysLogQuery.setThreePowers(true);
        }
        List<SysLog> sysLogs = getSysLogs(listSysLogQuery,user);
        long total = ((Page)sysLogs).getTotal();
        List<SysRequestLogVO> sysRequestLogVOS = getSysRequestLogDTO(sysLogs);
        return this.vList(sysRequestLogVOS,(int) total);
    }

    private List<SysLog> getSysLogs(ListSysLogQuery listSysLogQuery,User currentUser){
        SysLogQuery query = new SysLogQuery();
        BeanUtils.copyProperties(listSysLogQuery,query);
        if (StringUtils.isNotEmpty(listSysLogQuery.getRequestStartTime())) {
            query.setRequestStartTime(DateUtil.stringToDate(listSysLogQuery.getRequestStartTime(),DateUtil.DEFAULT_DATE_PATTERN));
        }
        if (StringUtils.isNotEmpty(listSysLogQuery.getRequestEndTime())) {
            query.setRequestEndTime(DateUtil.stringToDate(listSysLogQuery.getRequestEndTime(),DateUtil.DEFAULT_DATE_PATTERN));
        }
        Example example = this.pageQuery(query, SysLog.class);
        Example.Criteria criteria = example.createCriteria();
        if(listSysLogQuery.isThreePowers()){
            if(currentUser!=null){
                List<String> roleCodes = currentUser.getRoleCode();
                if (!roleCodes.isEmpty()) {
                    String roleCode = roleCodes.get(0);
                    if(RoleEnum.AUDIT.getRoleCode().equals(roleCode)) {
                        LinkedList list = new LinkedList();
                        list.add(RoleEnum.SYSCONTROLLER.getRoleName());
                        list.add(RoleEnum.SAFETER.getRoleName());
                        criteria.andIn("roleName",list);
                    }
                    else if (RoleEnum.SAFETER.getRoleCode().equals(roleCode)) {
                        LinkedList list = new LinkedList();
                        list.add(RoleEnum.AUDIT.getRoleName());
                        list.add(RoleEnum.SECRETMGR.getRoleName());
                        list.add(RoleEnum.BUSINESSMGR.getRoleName());
                        list.add(RoleEnum.OPERATIONMGR.getRoleName());
                        criteria.andIn("roleName",list);
                    } else if (RoleEnum.SECRETMGR.getRoleCode().equals(roleCode)) {
                        LinkedList list = new LinkedList();
                        list.add(RoleEnum.SECRETMGR.getRoleName());
                        list.add(RoleEnum.BUSINESSMGR.getRoleName());
                        list.add(RoleEnum.OPERATIONMGR.getRoleName());
                        criteria.andIn("roleName",list);
                    }
                    else if (RoleEnum.ADMIN.getRoleCode().equals(roleCode)) {
                        criteria.andNotEqualTo("roleName",RoleEnum.ADMIN.getRoleName());
                    } else {
                        criteria.andEqualTo("roleName", RoleEnum.NONEROLE.getRoleName());
                    }
                }
            }
        }
        example.and(criteria);
        List<SysLog> sysLogs = sysLogService.findByExample(example);
        return sysLogs;
    }

    private List<SysRequestLogVO> getSysRequestLogDTO(List<SysLog> list) {
        List<SysRequestLogVO> sysRequestLogVOS = new ArrayList<>();
        for (SysLog sysLog : list) {
            SysRequestLogVO sysRequestLogVO = new SysRequestLogVO();
            BeanUtils.copyProperties(sysLog,sysRequestLogVO);
            sysRequestLogVOS.add(sysRequestLogVO);
            if (sysLog.getLoginType()!=null) {
                Map loginTypes = new HashMap();
                loginTypes.put("id", sysLog.getLoginType());
                loginTypes.put("value", LoginTypeEnum.loginTypeEnumEscape(sysLog.getLoginType()));
                sysRequestLogVO.setLoginTypes(loginTypes);
            }
            if (sysLog.getType()!=null) {
                Map loginTypes = new HashMap();
                loginTypes.put("id", sysLog.getType());
                loginTypes.put("value", TypeEnum.typeEnum(sysLog.getType()));
                sysRequestLogVO.setTypes(loginTypes);
            }
            if (StringUtils.isNotBlank(sysLog.getUserName())) {
                sysRequestLogVO.setEventType(EventTypeEnum.eventTypeEscape(sysLog.getUserName()));
            }
        }
        return sysRequestLogVOS;
    }


    /**
     * @Description (通过Excel导出审计日志信息)
     */
    @GetMapping("/exportSyslogExcel")
    @ApiOperation(value = "导出审计日志信息,url需要按查询条件，拼接userName，description，loginType")
    @SysRequestLog(description="导出审计日志信息", actionType = ActionType.EXPORT,manually = false)
    public Result exportSyslogExcel(HttpServletResponse resp, HttpServletRequest request) throws UnsupportedEncodingException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("系统审计日志");
        List<String> titleList = new ArrayList<>();
        String titles = "流水号,用户标识,用户名称,单位名称,终端标识,请求方法名,请求方式,登录类型,操作类型,操作时间,操作条件,操作描述,操作结果";
        titleList = Arrays.asList(titles.split(","));
//        listSysLogQuery.setThreePowers(true);
        String userName = request.getParameter("userName");
        String description = request.getParameter("description");
        String requestStartTime = request.getParameter("requestStartTime");
        String requestEndTime = request.getParameter("requestEndTime");
        String requestIp = request.getParameter("requestIp");
        String type = request.getParameter("type");
        ListSysLogQuery listSysLogQuery = new ListSysLogQuery();
        if(StringUtils.isNotEmpty(requestIp)){
            listSysLogQuery.setRequestIp(requestIp);
        }
        if(StringUtils.isNotEmpty(userName)){
            listSysLogQuery.setUserName(userName);
        }
        if(StringUtils.isNotEmpty(type)){
            listSysLogQuery.setType(Integer.parseInt(type));
        }
        if(StringUtils.isNotEmpty(description)){
            listSysLogQuery.setDescription(description);
        }
        if (request.getParameter("loginType")!=null){
            listSysLogQuery.setLoginType(Integer.valueOf(request.getParameter("loginType")));
        }
        if (StringUtils.isNotEmpty(requestStartTime)) {
            listSysLogQuery.setRequestStartTime(requestStartTime);
        }
        if (StringUtils.isNotEmpty(requestEndTime)) {
            listSysLogQuery.setRequestEndTime(requestEndTime);
        }
        listSysLogQuery.setStart_(0);
        listSysLogQuery.setCount_(10000);
        listSysLogQuery.setOrder_("requestTime");
        listSysLogQuery.setBy_("desc");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Short confEnable = systemConfigService.getThreePowerEnable();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
            listSysLogQuery.setThreePowers(true);
        }
        List<SysLog> sysLogs = getSysLogs(listSysLogQuery,user);
        List<SysRequestLogVO> resultList = getSysRequestLogDTO(sysLogs);
        ExportExcelUtil.writeDateToCell(wb,resultList,sheet,titleList);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/vnd.ms-excel");
        resp.setHeader("Content-Disposition", "attachment;filename="
                + URLEncoder.encode("管理员日志_"+simpleDateFormat.format(new Date())+".xls", "UTF-8"));
        try {
            // 获取输出流
            OutputStream output = resp.getOutputStream();
            BufferedOutputStream bufferedOutPut = new BufferedOutputStream(
                    output);
            // 输出文件
            wb.write(bufferedOutPut);
            bufferedOutPut.flush();
            // 关闭流
            bufferedOutPut.close();
            wb.close();
        }
        catch (Exception e){
            logger.error("",e);
        }
        return  new Result("0", "系统日志导出成功");
    }

    @Ignore
    @PostMapping("/addSysLog")
    public ResultBody addSysLog(@RequestBody SystemLog systemLog,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        String userName = systemLog.getUserName();
        String requestIp = systemLog.getRequestIp();
        Integer type = systemLog.getType();

        if (StringUtils.isEmpty(userName)) {
            systemLog.setUserName(user.getName());
        }
        if (StringUtils.isEmpty(requestIp)) {
            systemLog.setRequestIp(IPUtils.getIpAddress(request));
        }
        if (StringUtils.isEmpty(systemLog.getDescription()) && ResourceUtil.getResourceDescMap() != null) {
            systemLog.setDescription(ResourceUtil.getResourceDescMap().get(systemLog.getRequestUrl()));
        }
        if(StringUtils.isEmpty(systemLog.getDescription())){
            return new ResultBody().ok();
        }
        systemLog.setOrganizationName(user.getOrgName());
        if (TypeEnum.BROSE.getCode().equals(type) || TypeEnum.SELECE.getCode().equals(type) || TypeEnum.EXPORT.getCode().equals(type)) {
            if (TypeEnum.BROSE.getCode().equals(type)) {
                systemLog.setRequestMethod(REQUEST_METHOD_GET);
            } else {
                systemLog.setRequestMethod(REQUEST_METHOD_POST);
            }
            List<UserRole> userRoles = userRoleService.findByProperty(UserRole.class, "userId", user.getId());
            String roleName = "";
            if (CollectionUtils.isNotEmpty(userRoles)) {
                Role role = roleService.findById(userRoles.get(0).getRoleId());
                if (role != null) {
                    roleName = role.getName();
                }
            }
            systemLog.setId(UUID.randomUUID().toString());
            systemLog.setRoleName(roleName);
            systemLog.setUserId(user.getIdcard());
            systemLog.setResponseResult(RESULT_SUCCESS);
        }
        SyslogSenderUtils.sendSyslogManually(systemLog);
        return new ResultBody().ok();
    }

    @GetMapping("loginThirtyDay")
    @Ignore
    public Result loginThirtyDay(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        List<String> roleCodes = user.getRoleCode();
        LinkedList list = new LinkedList();
        if (!roleCodes.isEmpty()) {
            String roleCode = roleCodes.get(0);
            if (RoleEnum.SECRETMGR.getRoleCode().equals(roleCode)) {
                list.add(RoleEnum.SECRETMGR.getRoleName());
                list.add(RoleEnum.BUSINESSMGR.getRoleName());
                list.add(RoleEnum.OPERATIONMGR.getRoleName());
            }
        }
        List<LoginThirtyDayVO> result = sysLogService.loginThirtyDay(null,list);
        return this.vList(result,result.size());
    }

    @PostMapping
    public VList<SysLog> getSysLog(@RequestBody Query query){
        Example example = pageQuery(query, SysLog.class);
        return this.vList(sysLogService.findByExample(example));
    }

    @GetMapping("/activeUserCount")
    @ApiOperation("活跃账户数量")
    @SysRequestLog(description="活跃账户数量", actionType = ActionType.SELECT)
    public VData getActiceUserCount() {
        Map<String,Object> map = new HashMap<>();
        Long activeCount = sysLogService.getActiceUserCount();
        map.put("activeCount",activeCount);
        return this.vData(map);
    }

    @GetMapping("/loginCount/{day}")
    @ApiOperation("账户登录次数top10")
    public VData getLoginCount(@PathVariable Integer day) {
        List<Map> list = sysLogService.getLoginCount(day);
        return this.vData(list);
    }

    @GetMapping("/loginTrend/{day}")
    @ApiOperation("登录趋势")
    public VData loginTrend(@PathVariable Integer day) {
        List<Map> list = sysLogService.loginTrend(day);
        return this.vData(list);
    }

    @GetMapping("/responseResultCount")
    @ApiOperation("操作成功失败比例")
    public VData getResponsResultCount() {
        List<Map> list = sysLogService.getResponsResultCount();
        return this.vData(list);
    }

    @GetMapping("/visitPageCount/common/{day}")
    @ApiOperation("频繁访问页面top10")
    public VData getCommonVisitPageCount(@PathVariable Integer day) {
        List<Map> list = sysLogService.getVisitPageCount(day,COMMON);
        return this.vData(list);
    }

    @GetMapping("/visitPageCount/unCommon/{day}")
    @ApiOperation("非频繁访问页面top10")
    public VData getUnCommonVisitPageCount(@PathVariable Integer day) {
        List<Map> list = sysLogService.getVisitPageCount(day,UNCOMMON);
        return this.vData(list);
    }

    @GetMapping("/responseErrorCount/{day}")
    @ApiOperation("操作异常统计")
    public VData getResponseErrorCount(@PathVariable Integer day) {
        List<Map> list = sysLogService.getResponseErrorCount(day);
        return this.vData(list);
    }

    @GetMapping("/operateTypeCount/{day}")
    @ApiOperation("操作类型统计")
    public VData getOperateTypeCount(@PathVariable Integer day) {
        List<Map> list = sysLogService.getOperateTypeCount(day);
        return this.vData(list);
    }
}
