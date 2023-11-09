package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "upgrade_record")
@Data
public class UpgradeRecordInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("文件名称")
    @Column(name = "file_name")
    private String fileName;

    @ApiModelProperty("文件大小")
    @Column(name = "file_size")
    private Double fileSize;

    @ApiModelProperty("升级说明")
    @Column(name = "upgrade_desc")
    private String upgradeDesc;

    @ApiModelProperty("升级时间")
    @Column(name = "upgrade_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date upgradeTime;

    // 升级结果：0 失败，1 成功
    @ApiModelProperty("升级结果")
    @Column(name = "result")
    private Integer result;

    @ApiModelProperty("失败信息")
    @Column(name = "message")
    private String message;
}