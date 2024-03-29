package com.vrv.vap.netflow.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.mapper.NetworkMonitorReportMapper;
import com.vrv.vap.netflow.model.NetworkMonitorReport;
import com.vrv.vap.netflow.service.NetworkMonitorReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 网络监测器实现类
 */
@Service
@Transactional
public class NetworkMonitorReportServiceImpl extends BaseServiceImpl<NetworkMonitorReport> implements NetworkMonitorReportService {

    @Resource
    NetworkMonitorReportMapper networkMonitorReportMapper;

    @Override
    public void deleteByDeviceId(String deviceId) {
        Example example = new Example(NetworkMonitorReport.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("deviceId",deviceId);
        example.and(criteria);
        networkMonitorReportMapper.deleteByExample(example);
    }
}
