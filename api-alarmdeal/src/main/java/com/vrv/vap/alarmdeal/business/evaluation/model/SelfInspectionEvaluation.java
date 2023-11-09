package com.vrv.vap.alarmdeal.business.evaluation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

/**
 * 自查自评结果表
 * @Date 2023-09
 * @author liurz
 */
@Data
@Table(name="self_inspection_evaluation")
@Entity
@ApiModel(value = "自查自评结果表")
public class SelfInspectionEvaluation {
    /**
     * 主键ID
     */
    @Id
    private String id;
    /**
     * 检查大类
     */
    @Column(name = "check_type")
    private String checkType;
    /**
     * 成因类型
     */
    @Column(name = "genetic_type")
    private String geneticType;
    /**
     * 待查部门名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 发生次数
     */
    @Column(name = "occur_count")
    private int occurCount;
    /**
     * 自查自评状态:自查自评状态(0:未开始,1:已自查自评)
     */
    @Column(name = "status")
    private int status;
    /**
     * 自查自评项产生时间
     */
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 自查自评开展时间
     */
    @Column(name = "ev_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date evTime;
    /**
     * 自查自评人员
     */
    @Column(name = "ev_user_name")
    private String evUserName;
    /**
     * 自查自评人员code
     */
    @Column(name = "ev_user_code")
    private String evUserCode;
    /**
     * 自查自评结果
     */
    @Column(name = "ev_result")
    private String evResult;
    /**
     * 整改情况
     */
    @Column(name = "rectification")
    private String rectification;
    /**
     * 关联事件ID(多个逗号分割)
     */
    @Column(name = "event_ids")
    private String eventIds;
    /**
     * 关联自查自评策略中间表id
     */
    @Column(name = "ref_process_id")
    private String refProcessId;
    /**
     * 关联策略id
     */
    @Column(name = "ref_id")
    private int refId;
}
