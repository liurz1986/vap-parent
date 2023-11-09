package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "alarm_query")
public class AlarmQuery {

    @Id
    @Column
    private String guid;  //主键guid

    @Column(name = "query_name")
    private String queryName;

    @Column(name = "query_condition")
    private String queryCondition;  //查询条件
    @Column(name = "user_id")
    private Integer userId;  //用户id
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;







}
