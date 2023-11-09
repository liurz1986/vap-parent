package com.vrv.vap.alarmdeal.business.asset.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 资产导入日志
 * 2021-07-30
 */
@Data
@Table(name="import_asset_log")
@Entity
@ApiModel(value = "资产导入日志")
public class AssetImportLog implements Serializable {

    @Id
    @Column(name="guid")
    private String guid; //ID

    @Column(name="user_name")
    private String userName; //用户名

    @Column(name="user_id")
    private String userId; //用户ID

    @Column(name="import_create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime; //导入开始时间

    @Column(name="import_finish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime; //导入完成时间

    @Column(name="import_asset_count")
    private Integer count; //导入的数量

    @Column(name="import_asset_result")
    private Integer result; //  导入结果：0表示成功、1表示失败、2导入进行中

    @Column(name="Import_asset_error_message")
    private String errorMessage; //导入错误信息
}
