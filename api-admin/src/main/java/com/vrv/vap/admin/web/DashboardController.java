package com.vrv.vap.admin.web;

import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.common.constant.ErrorCode;
import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.Dashboard;
import com.vrv.vap.admin.model.DashboardShare;
import com.vrv.vap.admin.service.DashboardService;
import com.vrv.vap.admin.service.DashboardShareService;
import com.vrv.vap.admin.vo.DashboardQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 仪表盘管理
 * @author lilang
 * @date 2018年7月27日
 */
@RestController
@RequestMapping(path = "/dashboard")
public class DashboardController extends ApiController {

    @Autowired
    DashboardService dashboardService;

    @Resource
    DashboardShareService dashboardShareService;

    @Value("${linkShare.username:admin}")
    private String username;

    @Value("${linkShare.password:vrv@123}")
    private String password;

//    @Autowired
//    UserClient userClient;

    /**
     * 保存仪表盘
     * @param dashboard
     * @return
     */
    @ApiOperation("保存仪表盘")
    @SysRequestLog(description = "保存仪表盘",actionType = ActionType.ADD)
    @PutMapping
    public Result saveDashboard(@RequestBody Dashboard dashboard) {
        dashboard.setTop(0);
        int result = dashboardService.save(dashboard);
        return this.result(result == 1);
    }


    /**
     * 获取仪表盘
     *
     * @param param
     * @return
     */
    @ApiOperation("获取仪表盘")
    @SuppressWarnings({"unchecked", "rawtypes"})
    @SysRequestLog(description = "获取仪表盘",actionType = ActionType.SELECT)
    @PostMapping
    public VList queryMapList(@RequestBody DashboardQuery param) {
        param.setOrder_("top");
        param.setBy_("desc");
        Example example = this.pageQuery(param, Dashboard.class);
        return this.vList(dashboardService.findByExample(example));
    }

    /**
     * 仪表盘列表
     * @return
     */
    @ApiOperation("查询仪表盘列表")
    @SysRequestLog(description = "查询仪表盘列表",actionType = ActionType.SELECT)
    @GetMapping
    public Result queryDashboardList() {
        return this.vData(dashboardService.findAll());
    }

    @ApiOperation("根据ID查询仪表盘")
    @SysRequestLog(description = "根据ID查询仪表盘",actionType = ActionType.SELECT)
    @GetMapping(value = "/{id}")
    public Result getDashboardById(@PathVariable Integer id) {
        if (id == null) {
            return this.vData(false);
        }
        return this.vData(dashboardService.findById(id));
    }


    /**
     * 修改仪表盘
     * @param dashboard
     * @return
     */
    @ApiOperation("修改仪表盘")
    @SysRequestLog(description = "修改仪表盘",actionType = ActionType.UPDATE)
    @PatchMapping
    public Result updateDashboard(@RequestBody Dashboard dashboard) {
        int result = dashboardService.updateSelective(dashboard);
        return this.result(result == 1);
    }

    /**
     * 删除仪表盘
     * @param param
     * @return
     */
    @ApiOperation("删除仪表盘")
    @SysRequestLog(description = "删除仪表盘",actionType = ActionType.DELETE)
    @DeleteMapping
    public Result deleteDashboard(@RequestBody DeleteQuery param) {
        int result = dashboardService.deleteByIds(param.getIds());
        return this.result(result >= 1);
    }

    /**
     * 删除仪表盘未配置图形
     * @return
     */
    @ApiOperation("删除仪表盘未配置图形")
    @SysRequestLog(description = "删除仪表盘未配置图形",actionType = ActionType.DELETE)
    @GetMapping(path="/delWidget")
    public Result delDashboardUnmatchedWidgets() {
	    List<Dashboard> DashboardList=dashboardService.findAll();
        int result=dashboardService.delUnmatchedWidgets(DashboardList);
        return this.result(result >= 1);
    }

    /**
     * 仪表盘链接分享登录
     */
//    @ApiOperation(value = "仪表盘链接分享登录")
//    @SysRequestLog(description = "仪表盘链接分享登录",actionType = ActionType.LOGIN)
//    @GetMapping(value = "/linkShareLogin")
//    public Result linkShareLogin(HttpServletRequest request) {
//		HttpSession session = request.getSession();
//		if(session.getAttribute(Global.SESSION.USER)==null){
//            UserLoginVo userLoginVo = new UserLoginVo();
//            userLoginVo.setUuu(username);
//            userLoginVo.setPpp(CommonTools.string2MD5(password));
//			return userClient.login(request.getHeader("Cookie"),userLoginVo);
//		}
//		return  Global.OK;
//    }

    /**
     * 仪表盘链接分享Token验证
     */
    @ApiOperation(value = "仪表盘链接分享Token验证")
    @GetMapping(value = "/validateToken/{token}")
    public Result validateToken(@PathVariable("token") String token) {
        Example example = new Example(DashboardShare.class);
        example.createCriteria().andEqualTo("token", token);
        List<DashboardShare> shareList = dashboardShareService.findByExample(example);
        if (CollectionUtils.isEmpty(shareList)) {
            return this.result(ErrorCode.TOKEN_INVALIDATE);
        }
        DashboardShare dashboardShare = shareList.get(0);
        String expireTime = TimeTools.format2(dashboardShare.getExpireTime());
        if (StringUtils.isEmpty(expireTime) || expireTime.compareTo(TimeTools.format2(new Date())) < 0) {
            return this.result(ErrorCode.TOKEN_OUT_DATE);
        }
        return Global.OK;
    }

    /**
     * 获取链接分享Token
     */
    @ApiOperation(value = "获取链接分享Token")
    @SysRequestLog(description = "获取链接分享Token",actionType = ActionType.SELECT)
    @GetMapping(value = "/getLinkShareToken/{dashboardid}")
    public Result getLinkShareToken(HttpServletRequest request,@ApiParam("仪表盘ID")@PathVariable("dashboardid") Integer dashboardid) {
        HttpSession session = request.getSession();
        String encodeStr = CommonTools.generateId() + "|" + session.getId() + "|" + dashboardid.toString();
        String token = DigestUtils.md5Hex(encodeStr);
        return this.vData(token);
    }

    /**
     * 置顶仪表盘
     * @param dashboard
     * @return
     */
    @ApiOperation("置顶仪表盘")
    @SysRequestLog(description = "置顶仪表盘",actionType = ActionType.UPDATE)
    @PatchMapping(path="/top")
    public Result updateTopDashboard(@RequestBody Dashboard dashboard) {
        if (dashboard == null || dashboard.getId() == null) {
            return this.vData(false);
        }
        int result = dashboardService.updateTopDashboard(dashboard);
        return this.result(result == 1);
    }
}
