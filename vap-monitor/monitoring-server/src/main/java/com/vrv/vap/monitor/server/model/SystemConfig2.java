package com.vrv.vap.monitor.server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 系统配置
 */
@ApiModel("系统配置信息")
@Data
@Table(name = "tb_conf")
public class SystemConfig2 {
    @ApiModelProperty("配置类型ID")
    @Id
    @Column(name = "conf_id")
    private String confId;


    @ApiModelProperty("配置是否开启标志")
    @Column(name = "conf_enable")
    private Byte confEnable;


    @ApiModelProperty("配置最近新时间")
    @Column(name = "conf_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",locale="zh",timezone="GMT+8")
    private Date confTime;


    @ApiModelProperty("配置值")
    @Column(name = "conf_value")
    private String confValue;

    @ApiModelProperty("更新状态标志")
    @Column(name = "status_Update")
    private Byte statusUpdate;
}
