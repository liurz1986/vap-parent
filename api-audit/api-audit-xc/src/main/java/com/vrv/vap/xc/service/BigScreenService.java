package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.model.AttackModel;
import com.vrv.vap.xc.model.DevModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.PrintTimeModel;

import java.util.List;
import java.util.Map;

public interface BigScreenService {

    /**
     *被攻击ip个数
     * @param sysId
     * @return
     */
    VData<Integer> attackIpCount(AttackModel model);

    /**
     * 被攻击应用排行
     * @return
     */
    VData<List<Map<String,Object>>> attackSysCount(PageModel model);
    /**
     * 被攻击事件类别
     * @return
     */
    VData<List<Map<String,Object>>> attackType(PageModel model);

    /**
     * 攻击趋势变化
     * @return
     */
    VData<List<Map<String,Object>>> attackTrend(PrintTimeModel model);


    /**
     * 用户行为态势-访问设备排名
     * @return
     */
    VData<List<Map<String,Object>>> userVisitDev(PageModel model);


    /**
     * CPU排行
     * @return
     */
    VData<List<Map<String,Object>>> cpuTrend(PageModel model);

    /**
     * 内存排行
     * @return
     */
    VData<List<Map<String,Object>>> memoryTrend(PageModel model);

    /**
     * 磁盘排行
     * @return
     */
    VData<List<Map<String,Object>>> diskTrend(PageModel model);
    /**
     * 访客统计排名
     * @return
     */
    VData<List<Map<String,Object>>> visitRank(PageModel model);

    /**
     * 攻击态势-实时攻击分布
     * @return
     */
    VData<List<Map<String,Object>>> attackDistribution(PageModel model);
    /**
     * 攻击态势-实时监测
     * @return
     */
    VData<List<Map<String,Object>>> attackMonitor(PageModel model);
}
