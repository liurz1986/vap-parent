package com.vrv.vap.monitor.entity;

import lombok.Data;

@Data
public class AssetStatistic {

    private int connectCount;
    private int unConnectCount;
    private int unMonitorCount;
}
