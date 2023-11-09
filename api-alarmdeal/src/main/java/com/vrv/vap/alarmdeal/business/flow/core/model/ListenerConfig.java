package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 自定义监听器配置
 *
 * 2022-11-7
 */
@Data
@Table(name="t_flow_listener_config")
@Entity
public class ListenerConfig {
    @Id
    @Column(name="listener_code",length = 255)
    private String listenerCode;  //监听器code

    @Column(name="listener_name",length = 255)
    private String listenerName;  // 监听器名称

    @Column(name="status",length = 10)
    private String status;  // 是否开启，默认开启 "0"表示开启，"1"表示取消

    @Column(name="type",length = 10)
    private String type;  // 监听器类型 ： 任务监听器、

}
