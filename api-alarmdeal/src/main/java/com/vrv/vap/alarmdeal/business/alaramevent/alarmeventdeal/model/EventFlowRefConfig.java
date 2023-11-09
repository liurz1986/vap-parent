package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 事件分类关联流程配置表
 * @author liurz
 * @date 202310
 */
@Table(name="flow_event_ref_config")
@Entity
@Data
public class EventFlowRefConfig {
    /**
     * 事件分类
     */
    @Id
    @Column(name = "event_type")
    private String eventType;
    /**
     * 事件分类名称
     */
    @Column(name = "event_type_name")
    private String eventTypeName;
    /**
     * 流程名称
     */
    @Column(name = "flow_name")
    private String flowName;

}
