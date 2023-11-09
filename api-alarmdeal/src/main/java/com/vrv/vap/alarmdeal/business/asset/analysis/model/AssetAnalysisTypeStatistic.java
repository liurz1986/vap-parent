package com.vrv.vap.alarmdeal.business.asset.analysis.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 台账资产数量变化统计：按二级资产类型统计
 */
@Data
@Table(name="asset_analysis_type_statistic")
@Entity
@ApiModel(value = "台账资产数量变化统计")
public class AssetAnalysisTypeStatistic {
    @Id
    @Column(name="guid")
    private String guid ; //主键

    @Column(name="create_time")
    private Date createTime;  // 统计时间

    @Column(name="num")
    private int num;   //数量

    @Column(name="name")
    private String name;   //二级资产类型名称

}
