package com.vrv.vap.alarmdeal.business.appsys.controller.query;

import com.vrv.vap.alarmdeal.business.appsys.service.query.InternetInfoManageQueryService;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.InternetInfoManageQueryVO;
import com.vrv.vap.es.enums.ResultCodeEnum;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


/**
 * 互联信息报表接口
 * 2023-07-05
 * @author liurz
 */
@RestController
@RequestMapping(value="/interNetInfoManageQuery")
public class InternetInfoManageQueryController {
    private static Logger logger = LoggerFactory.getLogger(InternetInfoManageQueryController.class);
    @Autowired
    private InternetInfoManageQueryService internetInfoManageQueryService;
    /**
     * 互联单位统计列表
     * 2023-07-05
     * *@return
     */
    @PostMapping(value = "/tabulation")
    @ApiOperation(value = "互联单位统计列表", notes = "")
    public Result<List<InternetInfoManageQueryVO>> tabulation() {
        try {
            List<InternetInfoManageQueryVO> list = internetInfoManageQueryService.tabulation();
            return ResultUtil.successList(list);
        } catch (Exception e) {
            logger.error("互联单位统计列表异常", e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "互联单位统计列表异常");
        }
    }
}
