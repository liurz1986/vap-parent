package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class TerminalLoginLog {
    @SerializedName("std_user_no")
    private String stdUserNo;
    @SerializedName("dev_ip")
    private String devIp;
    @SerializedName("op_type")
    private Integer opType;
    @SerializedName("event_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventTime;
}
