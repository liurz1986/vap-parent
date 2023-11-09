package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.ChangeRiskReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterFieldReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterSourceReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.res.FilterSourceRes;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: 梁国露
 * @since: 2022/11/28 15:40
 * @description:
 */
public interface AlarmCommonDataService {
    /**
     * 规则字段重排序
     * @param req
     * @return
     */
    Boolean filterColumnFresh(FilterFieldReq req);

    /**
     *
     * @param req
     * @return
     */
    Map<String,List<FilterSourceRes>> updateFilterColumn(FilterSourceReq req);

    /**
     *
     * @param req
     * @return
     */
    List<String> changeRiskList(ChangeRiskReq req);

    /**
     * 通过name删除redis信息
     * @param name
     */
    List<String> deleteRedisData(String name);

    /**
     * 通过name删除redis信息
     * @param name
     */
    String getRedisData(String name);

    /**
     * 通过name删除redis信息
     * @param name
     */
    List<String> getRedisKeysData(String name);

    /**
     * 通过name删除redis信息
     * @param name
     */
    Set<String> getRedisKeysList(String name);

    /**
     * 变更数据源ID信息
     * @return
     */
    Boolean getDataSource();

    Boolean handleAlarmEsData();
}
