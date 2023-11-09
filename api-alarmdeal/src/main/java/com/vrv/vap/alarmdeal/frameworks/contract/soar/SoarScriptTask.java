package com.vrv.vap.alarmdeal.frameworks.contract.soar;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "soar_script_task")
public class SoarScriptTask {

    public static int statusDelete=-2; //删除

    @Id
    @Column
    private String guid;

    @Column(name = "task_name")
    private String taskName;

    @Column(name = "script_guid")
    private String scriptGuid;

    @Column
    private Integer priority;

    @Column(name="run_status")
    private Integer runStatus;

    @Column(name="process_instance_id")
    private String processInstanceId;

    @Column(name="create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;  //创建时间

    @Column(name="update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date updateTime; //更新时间


    @Column(name="arg_input")
    private  String argInput;

    @Column(name="arg_output")
    private  String argOutput;



}
