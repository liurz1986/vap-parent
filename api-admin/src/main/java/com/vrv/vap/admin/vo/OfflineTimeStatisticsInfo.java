package com.vrv.vap.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OfflineTimeStatisticsInfo {
    private String userNo;
    private LocalDateTime currentTime;
}
