package com.vrv.vap.xc.service.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;
import org.springframework.web.bind.annotation.RequestBody;

public interface ClueInfoIndicatorsService {
    /**
     * 5.风险事件成因分析情况
     * 按事件成因类型进行分析
     *
     * @param model
     * @return
     */
    VList<EventSortModel> statisticsCauseTypeTimes(@RequestBody ReportParam model, String countField);

    /**
     * 4.线索信息报送情况
     * 4.5.线索信息类型分布和排名
     *
     * @param model
     * @return
     */
    VList<EventSortModel> statisticsClueInfoType(@RequestBody ReportParam model, String countField);

    /**
     * 4.线索信息报送情况
     * 4.1.线索信息总数
     *
     * @param model
     * @return
     */
    VList<ClueInfoModel> statisticsClueInfoTotal(@RequestBody ReportParam model);

    /**
     * 4.线索信息报送情况
     * 4.2.统计新增线索信息数量
     *
     * @param model
     * @return
     */
    VList<ClueInfoModel> statisticsNewlyAddedClueInfo(@RequestBody ReportParam model);
}
