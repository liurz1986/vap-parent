package com.vrv.vap.alarmdeal.business.analysis.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "alarm_response_log")
public class AlarmResponseLog {

    @Id
    @Column
    private String guid;

    @Column(name = "alarm_name")
    private String alarmName;

    @Column(name = "response_type")
    private  String responseType;

    @Column(name = "response_result")
    private  String responseResult;

    @Column(name = "response_reason")
    private  String responseReason;

    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyyMMdd HH:mm:ss",timezone ="GMT+8" )
    private Date createTime=new Date();

}
