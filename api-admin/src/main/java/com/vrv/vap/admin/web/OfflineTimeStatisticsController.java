package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.OfflineTimeStatistics;
import com.vrv.vap.admin.service.OfflineTimeStatisticsService;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsPageQuery;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsQuery;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsVo;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 离线时长统计
 */
@RestController
@RequestMapping("/offline")
@Api(value = "离线时长统计")
public class OfflineTimeStatisticsController extends ApiController {

    @Autowired
    private OfflineTimeStatisticsService offlineTimeStatisticsService;

    /**
     * 离线时长查询
     *
     * @param statisticsQuery
     * @return
     */
    @PostMapping
    @ApiOperation(value = "分页查询")
    public VList<OfflineTimeStatistics> getPage(@RequestBody OfflineTimeStatisticsPageQuery statisticsQuery) {
        Example example = this.pageQuery(statisticsQuery, OfflineTimeStatistics.class);
        return this.vList(offlineTimeStatisticsService.findByExample(example));
    }

    /**
     * 离线时长比统计
     *
     * @param query
     * @return
     */
    @PostMapping("/calcOfflineTime")
    @ApiOperation(value = "离线时长比统计")
    public VList<OfflineTimeStatisticsVo> calcOfflineTime(@RequestBody OfflineTimeStatisticsQuery query) {
        List<OfflineTimeStatisticsVo> offlineTime = offlineTimeStatisticsService.calcOfflineTime(query);
        return this.vList(offlineTime, offlineTime.size());
    }
}
