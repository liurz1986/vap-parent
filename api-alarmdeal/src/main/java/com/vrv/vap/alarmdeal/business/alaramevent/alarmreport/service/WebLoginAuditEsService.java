package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo.WebLoginAudit;

import java.util.List;

public interface WebLoginAuditEsService {
    /**
     * 查询数据
     * @param guids
     * @param reportDevType
     * @return
     */
    public List<WebLoginAudit> findAll(List<String> guids,String reportDevType);
}
