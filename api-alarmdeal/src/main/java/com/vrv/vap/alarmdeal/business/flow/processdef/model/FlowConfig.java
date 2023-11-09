package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="t_flow_config")
@Data
public class FlowConfig {
    @Id
    private String code;
    @Column(name="value")
    private String value;
    @Column(name="description")
    private String description;

}
