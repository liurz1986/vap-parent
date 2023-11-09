package com.vrv.vap.alarmdeal.business.asset.analysis.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 在线资产统计
 */
@Data
@Table(name="asset_analysis_online_statistic")
@Entity
@ApiModel(value = "在线资产统计")
public class AssetAnalysisOnLineStatistic {
    @Id
    @Column(name="guid")
    private String guid ; //主键

    @Column(name="create_time")
    private Date createTime;  // 统计时间

    @Column(name="num")
    private int num;   //数量
}
