package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.analysis.model.IpTable;
import com.vrv.vap.alarmdeal.business.analysis.repository.IpTableRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.IpTableService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.VapUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IpTableServiceImpl  extends BaseServiceImpl<IpTable, String> implements IpTableService {

    @Autowired
    private IpTableRespository ipTableRespository;

    @Override
    public IpTableRespository getRepository() {
        return ipTableRespository;
    }

    private static Logger logger = LoggerFactory.getLogger(WarnResultForESService.class);

    @Override
    public void constructAreaData(Map<String, Object> map, String src_ips) {
        Long ip2int = VapUtil.ip2int(src_ips);
        List<QueryCondition> conditions=new ArrayList<>();
        conditions.add(QueryCondition.lt("startIpNum",ip2int));
        conditions.add(QueryCondition.gt("endIpNum",ip2int));
        List<IpTable> ipTableList=findAll(conditions);
        if(ipTableList.size()==1){
            IpTable ipTable=ipTableList.get(0);
            map.put("areaName", ipTable.getCountry());
        }
    }
}
