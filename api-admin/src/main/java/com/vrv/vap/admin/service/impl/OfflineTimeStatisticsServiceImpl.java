package com.vrv.vap.admin.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.vrv.vap.admin.model.OfflineTimeStatistics;
import com.vrv.vap.admin.service.OfflineTimeStatisticsService;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsQuery;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsVo;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OfflineTimeStatisticsServiceImpl extends BaseServiceImpl<OfflineTimeStatistics> implements OfflineTimeStatisticsService {

    // 工作时长
    private static final Integer WORK_TIME = 480;

    /**
     * 查询终端登录数据
     *
     * @param userNo    员工编号
     * @param loginType 登录类型
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 终端登录日志列表
     */
    public List<OfflineTimeStatistics> getTerminalLogByTime(String userNo, Integer loginType, String beginTime, String endTime) {
        Example example = new Example(OfflineTimeStatistics.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userNo", userNo);
        criteria.andEqualTo("loginType", loginType);
        criteria.andBetween("currentDay", beginTime, endTime);
        return this.findByExample(example);
    }

    /**
     * 计算离线时长比例
     *
     * @param query 查询对象
     * @return 条件时间内对应离线比例
     */
    public List<OfflineTimeStatisticsVo> calcOfflineTime(OfflineTimeStatisticsQuery query) {
        List<OfflineTimeStatistics> terminalLogByTime = getTerminalLogByTime(query.getUserNo(), query.getLoginType(),
                query.getBeginTime(), query.getEndTime());
        Map<Date, List<OfflineTimeStatistics>> collect = terminalLogByTime.stream().collect(Collectors.groupingBy(OfflineTimeStatistics::getCurrentDay));
        List<OfflineTimeStatisticsVo> result = new ArrayList<>();
        for (Map.Entry<Date, List<OfflineTimeStatistics>> entries : collect.entrySet()) {
            List<OfflineTimeStatistics> value = entries.getValue();
            if (CollectionUtils.isNotEmpty(value)) {
                Integer countTime = 0;
                for (OfflineTimeStatistics statistics : value) {
                    countTime += statistics.getCountTime();
                }
                BigDecimal offTime = new BigDecimal(countTime);
                BigDecimal workTime = new BigDecimal(WORK_TIME);
                BigDecimal offline = offTime.divide(workTime, 2, BigDecimal.ROUND_HALF_UP);
                OfflineTimeStatisticsVo vo = new OfflineTimeStatisticsVo();
                vo.setCurrentTime(entries.getKey());
                vo.setOffline(BigDecimal.valueOf(offline.floatValue() * 100).setScale(0, RoundingMode.HALF_UP).floatValue() + "%");
                result.add(vo);
            }
        }
        return result;
    }
}
