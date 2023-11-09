package com.vrv.vap.netflow.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;

import java.util.List;

/**
 * @author sj
 * @version 1.0
 * @date 2023/10/8 16:23
 * @program: api-netflow
 * @description:
 */


public interface NetworkMonitorAuditedMapper extends BaseMapper<NetworkMonitorAudited> {

    /**
     *  获取未注册（审核）注册器列表
     * @return
     */
    List<NetworkMonitor> getUnAuditList();
    /**
     *  获取未注册（审核）注册器列表 分页
     * @return
     */
   // List<NetworkMonitor> getUnAuditPage(Query query);


    /**
     * 获取未注册(审核)注册器数量
     * @return
     */
     Integer  getUnauditCount();
}
