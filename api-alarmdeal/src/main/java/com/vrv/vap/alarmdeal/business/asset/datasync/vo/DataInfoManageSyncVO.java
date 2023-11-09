package com.vrv.vap.alarmdeal.business.asset.datasync.vo;


import lombok.Data;


/**
 * 数据信息管理kafka数据
 *
 * 2022-05-27
 */

@Data
public class DataInfoManageSyncVO {

    private Integer id;
    private String dataFlag; // 数据标识
    private String businessType; // 业务类型
    private String secretLevel; // 涉密等级
    private String fileName; // 文件名称
    private String fileType; // 文件类型
    private String fileSize; // 文件大小 (单位MB)
    private String fileStatus; //文件管理状态 字典：b61a841f-3f90-39c6-eb54-a65f8c5261f9
    private String draftUser; // 文件起草人
    private String determineUser; // 文件定密人
    private String saleUser; // 文件签发人
    private String awareScope; // 知悉范围
    private String secretPeriod; // 保密期限
    private String determineReason; // 定密依据
    private String fileAuth;  // 文件授权
    private int dataSourceType;   //数据来源类型：1、手动录入；2 数据同步；3资产发现
    private String syncSource;   //外部来源信息 北信源融一：bxy-ry，北信源准入：bxy-zr，北信源主审：bxy-zs
    private String syncUid;   //外部来源主键ID

}

