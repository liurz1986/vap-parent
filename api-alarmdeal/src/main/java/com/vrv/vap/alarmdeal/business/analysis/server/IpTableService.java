package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.analysis.model.IpTable;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.Map;

public interface IpTableService extends BaseService<IpTable, String> {

    public void constructAreaData(Map<String, Object> map, String src_ips);
}
