package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.EventSortModel;
import com.vrv.vap.xc.model.EventTypeModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.service.report.EventIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-事件指标
 */
@RestController
@RequestMapping("/report/event")
public class EventIndicatorsController {

    @Resource
    private EventIndicatorsService eventIndicatorsService;

    /**
     * 事件发生与处置情况
     * 统计事件总数，各类型事件数量并排序
     * @param model
     * @return
     */
    @PostMapping("/statisticsEventTypeTimes")
    public VList<EventSortModel> statisticsEventTypeTimes(@RequestBody ReportParam model) {
        return eventIndicatorsService.statisticsEventTypeTimes(model);
    }

    /**
     * 事件发生与处置情况
     * 统计已处置数量、待处置数量
     * @param model
     * @return
     */
    @PostMapping("/statisticsEventStatusTimes")
    public VList<EventSortModel> statisticsEventStatusTimes(@RequestBody ReportParam model) {
        return eventIndicatorsService.statisticsEventStatusTimes(model);
    }

    /**
     * 1.7.协查、协办情况
     * 协查、协办事件总数，已处置数量和未处置数量
     */
    @PostMapping("/statisticsInvestigationTotal")
    public VList<EventTypeModel> statisticsInvestigationTotal(@RequestBody ReportParam model, boolean total) {
        return eventIndicatorsService.statisticsInvestigationTotal(model, total);
    }

    /**
     * 3.3.各部门新增的事件情况
     * 统计各部门新增的事件数量
     */
    @PostMapping("/statisticsDepartmentEvents")
    public VList<EventSortModel> statisticsDepartmentEvents(@RequestBody ReportParam model) {
        return eventIndicatorsService.statisticsDepartmentEvents(model);
    }

}
