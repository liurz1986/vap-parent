package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OfflineTimeStatisticsVo {
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date currentTime;
    private String offline;
}
