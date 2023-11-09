package com.vrv.vap.netflow.vo;

import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;
import lombok.Data;

/**
 * @author sj
 * @version 1.0
 * @date 2023/10/25 9:47
 * @program: api-netflow
 * @description: NetworkMonitorCurrentStatus
 */


@Data
public class NetworkMonitorCurrentStatusVO extends NetworkMonitorCurrentStatus
{
    private  String apiKey;
}
