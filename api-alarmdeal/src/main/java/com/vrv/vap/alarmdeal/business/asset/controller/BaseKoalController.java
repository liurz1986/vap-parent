package com.vrv.vap.alarmdeal.business.asset.controller;

import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetOrgTreeVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/baseKoal")
public class BaseKoalController {

    @Autowired
    private AssetService assetService;

    @GetMapping(value="/organizationByCode")
    @ApiOperation(value="机构树（带应用）（自带权限）",notes="")
    @SysRequestLog(description="机构树（带应用）（自带权限）", actionType = ActionType.SELECT,manually = false)
    public Result<AssetOrgTreeVO> organizationByCode(){
        AssetOrgTreeVO  assetOrgTreeVO=assetService.organizationByCode();
        return ResultUtil.success(assetOrgTreeVO);
    }

}
