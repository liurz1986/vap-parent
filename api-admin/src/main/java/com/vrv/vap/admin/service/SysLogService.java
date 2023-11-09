package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.SysLog;
import com.vrv.vap.admin.vo.ListSysLogQuery;
import com.vrv.vap.admin.vo.LoginThirtyDayVO;
import com.vrv.vap.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author huipei.x
 * @data 创建时间 2019/6/14
 * @description 类说明 :
 */
public interface SysLogService extends BaseService<SysLog> {
    List querySysLog(ListSysLogQuery listSysLogQuery, List<String> orgNameList);
    long querySysLogCount(ListSysLogQuery listSysLogQuery, List<String> orgNameList);
    List<LoginThirtyDayVO> loginThirtyDay(List<String> orgNameList,List<String> roleNameList);

    long getActiceUserCount();

    List<Map> getLoginCount(Integer day);

    List<Map> loginTrend(Integer day);

    List<Map> getResponsResultCount();

    List<Map> getVisitPageCount(Integer day,String type);

    void cleanSyslog(Integer cleanDate);

    List<Map> getResponseErrorCount(Integer day);

    List<Map> getOperateTypeCount(Integer day);
}