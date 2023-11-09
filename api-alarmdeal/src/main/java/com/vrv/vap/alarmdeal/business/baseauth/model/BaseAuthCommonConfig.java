package com.vrv.vap.alarmdeal.business.baseauth.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 审批类型基础配置表
 *
 * @author liurz
 * @date 202308
 */
@Data
@Table(name="base_auth_common_config")
@Entity
@ApiModel(value = "审批类型基础配置表")
public class BaseAuthCommonConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="conf_id")
    private String confId; //id

    @Column(name="conf_value")
    private String confValue ;//value的值

    @Column(name="conf_time")
    private String confTime; //时间

    @Column(name="remark")
    private String remark; //备注
}
