package com.vrv.vap.xc.controller.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.report.ClueInfoIndicatorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 报表-线索信息指标
 */
@RestController
@RequestMapping("/report/clueInfo")
public class ClueInfoIndicatorsController {
    @Resource
    private ClueInfoIndicatorsService clueInfoIndicatorsService;

    /**
     * 5.风险事件成因分析情况
     * 按事件成因类型进行分析
     * @param model
     * @return
     */
    @PostMapping("/statisticsCauseTypeTimes")
    public VList<EventSortModel> statisticsCauseTypeTimes(@RequestBody ReportParam model) {
        return clueInfoIndicatorsService.statisticsCauseTypeTimes(model,"cause_type");
    }

    /**
     * 4.线索信息报送情况
     * 4.5.线索信息类型分布和排名
     * @param model
     * @return
     */
    @PostMapping("/statisticsClueInfoType")
    public VList<EventSortModel> statisticsClueInfoType(@RequestBody ReportParam model) {
        return clueInfoIndicatorsService.statisticsClueInfoType(model,"event_type");
    }

    /**
     * 4.线索信息报送情况
     * 4.1.线索信息总数
     * @param model
     * @return
     */
    @PostMapping("/statisticsClueInfoTotal")
    public VList<ClueInfoModel> statisticsClueInfoTotal(@RequestBody ReportParam model) {
        return clueInfoIndicatorsService.statisticsClueInfoTotal(model);
    }

    /**
     * 4.线索信息报送情况
     * 4.2.统计新增线索信息数量
     * @param model
     * @return
     */
    @PostMapping("/statisticsNewlyAddedClueInfo")
    public VList<ClueInfoModel> statisticsNewlyAddedClueInfo(@RequestBody ReportParam model) {
        return clueInfoIndicatorsService.statisticsNewlyAddedClueInfo(model);
    }
}
