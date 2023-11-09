package com.vrv.vap.alarmdeal.business.analysis.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "rule_model_Of_Asset_type")
public class RuleModelOfAssetType {


    @Id
    @Column(name="guid")
    private String guid;

    @Column(name="analysis_id")
    private String analysisId; //分析器模板code

    @Column(name = "asset_type")
    private String assetType;

    @Column(name="param_config")
    private String paramConfig; //参数配置项



}
