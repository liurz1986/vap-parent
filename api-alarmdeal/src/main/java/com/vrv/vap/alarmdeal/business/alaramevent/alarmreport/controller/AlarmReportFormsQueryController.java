package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.AlarmReportFormsQueryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteResultVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.AlarmUniteSearchVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 613告警报表信息
 *
 *
 * 2023-3-23
 * @author liurz
 */
@RestController
@RequestMapping("alarmReportForms")
public class AlarmReportFormsQueryController {
    private static Logger logger = LoggerFactory.getLogger(AlarmReportFormsQueryController.class);

    @Autowired
    private AlarmReportFormsQueryService alarmUniteQueryService;

    /**
     * 报表查询告警关联信息
     *  条件
     *  startTime;  // 开始时间
     *  endTime;  // 结束时间
     *  reportDevType; // 上报类型
     * {"startTime":"2023-03-22 13:46:58","endTime":"2023-03-23 13:46:58","reportDevType":"RD04"}
     * @param alarmUniteSearchVO
     * @return
     */
    @PostMapping("query")
    @ApiOperation(value="告警报表信息",notes="")
    @SysRequestLog(description = "告警报表信息", actionType = ActionType.SELECT, manually = false)
    public Result<List<AlarmUniteResultVO>> queryAlarmDetail(@RequestBody AlarmUniteSearchVO alarmUniteSearchVO, @RequestParam("reportDevType") String reportDevType){
        try {
            if(StringUtils.isBlank(reportDevType)){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "reportDevType不能为空!");
            }
            alarmUniteSearchVO.setReportDevType(reportDevType);
            if(StringUtils.isBlank(alarmUniteSearchVO.getStartTime())||StringUtils.isBlank(alarmUniteSearchVO.getEndTime())){
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "开始时间或结束时间不能为空!");
            }
            return  ResultUtil.successList(alarmUniteQueryService.queryAlarmDetail(alarmUniteSearchVO));
        } catch (Exception e) {
            logger.error("告警报表信息异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "告警报表信息异常");
        }
    }

}
