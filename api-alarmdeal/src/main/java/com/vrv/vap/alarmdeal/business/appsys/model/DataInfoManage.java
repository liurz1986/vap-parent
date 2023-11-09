package com.vrv.vap.alarmdeal.business.appsys.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author lps 2021/8/10
 */

@Data
@Entity
@Table(name = "data_info_manage")
@ApiModel(value = "数据信息管理")
public class DataInfoManage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty(value = "数据标识")
    @Column(name = "data_flag")
    private String dataFlag;
    @ApiModelProperty(value = "业务类型")
    @Column(name = "business_type")
    private String businessType;
    @ApiModelProperty(value = "涉密等级")
    @Column(name = "secret_level")
    private String secretLevel;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;
    // 新增字段   2022-05-27
    @Column(name="file_name")
    private String fileName; // 文件名称
    @Column(name="file_type")
    private String fileType; // 文件类型
    @Column(name="file_size")
    private String fileSize; // 文件大小 (单位MB)
    @Column(name="file_status")
    private String fileStatus; //文件管理状态
    @Column(name="draft_user")
    private String draftUser; // 文件起草人  json数据
    @Column(name="datermine_user")
    private String determineUser; // 文件定密人
    @Column(name="sale_user")
    private String saleUser; // 文件签发人
    @Column(name="aware_scope")
    private String awareScope; // 知悉范围 json数据
    @Column(name="sercet_period")
    private String secretPeriod; // 保密期限 json数据
    @Column(name="datermine_reason")
    private String determineReason; // 定密依据 json数据
    @Column(name="file_auth")
    private String fileAuth;  // 文件授权 json数据
    @Column(name="data_source_type")
    private int dataSourceType =1;   //数据来源类型：1、手动录入；2 数据同步；3资产发现  默认手动录入
    @Column(name="sync_source")
    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
    @Column(name="sync_uid")
    private String syncUid;   //外部来源主键ID

}
