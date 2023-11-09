package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.EventSortModel;
import com.vrv.vap.xc.model.EventTypeModel;
import com.vrv.vap.xc.model.ReportParam;
import org.springframework.web.bind.annotation.RequestBody;

public interface EventIndicatorsService {
    /**
     * 事件发生与处置情况
     * 统计事件总数，各类型事件数量并排序
     *
     * @param model
     * @return
     */
    VList<EventSortModel> statisticsEventTypeTimes(@RequestBody ReportParam model);

    /**
     * 事件发生与处置情况
     * 统计已处置数量、待处置数量
     *
     * @param model
     * @return
     */
    VList<EventSortModel> statisticsEventStatusTimes(@RequestBody ReportParam model);

    /**
     * 1.7.协查、协办情况
     * 协查、协办事件总数，已处置数量和未处置数量
     */
    VList<EventTypeModel> statisticsInvestigationTotal(@RequestBody ReportParam model,boolean total);

    /**
     * 3.3.各部门新增的事件情况
     * 统计各部门新增的事件数量
     */
    VList<EventSortModel> statisticsDepartmentEvents(@RequestBody ReportParam model);
}
