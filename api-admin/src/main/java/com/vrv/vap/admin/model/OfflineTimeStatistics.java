package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "offline_time_statistics")
public class OfflineTimeStatistics {
    @Id
    private Integer id;
    private String ip;
    private String userNo;
    private String departmentName;
    private String userName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date logoutTime;
    private Integer countTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date currentDay;
    // 1登录 2注销
    private Integer loginType;
}
