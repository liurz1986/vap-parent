package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table threat_malicious_files
 *
 * @mbg.generated do_not_delete_during_merge 2018-11-20 16:16:43
 */
@ApiModel
@SuppressWarnings("unused")
public class ThreatMaliciousFilesQuery extends Query {
    /**
     * 类别描述
     */
    @ApiModelProperty("类别描述")
    private String categoryDesc;

    /**
     * 数据类型包括：md5（辰信领创）、feed_hash-md5、feed_hash-sha、feed_hash-sha256
     */
    @ApiModelProperty("数据类型包括：md5（辰信领创）、feed_hash-md5、feed_hash-sha、feed_hash-sha256")
    private String dataType;

    /**
     * 情报评定时间 yyyy-mm-dd hh:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("情报评定时间 yyyy-mm-dd hh:mm:ss")
    private Date evaluateTime;

    /**
     * 地理位置
     */
    @ApiModelProperty("地理位置")
    private String geo;

    /**
     *
     */
    @ApiModelProperty("")
    @NotNull
    private String id;

    /**
     * 情报来源 1 北信源 2天际友盟 3 辰信领创 4 安数云
     */
    @ApiModelProperty("情报来源 1 北信源 2天际友盟 3 辰信领创 4 安数云")
    private Integer informationSource;

    /**
     * 情报评定时间 yyyy-mm-dd hh:mm:ss
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("情报评定时间 yyyy-mm-dd hh:mm:ss")
    private Date insertTime;

    /**
     * 恶意文件名称
     */
    @ApiModelProperty("恶意文件名称")
    private String maliciousName;

    /**
     * 恶意文件类别：TI、c2、gambling、malware、phishing、porn、proxy、ransomware、scanner、spam、tor
     */
    @ApiModelProperty("恶意文件类别：TI、c2、gambling、malware、phishing、porn、proxy、ransomware、scanner、spam、tor")
    private String reputationCategory;

    /**
     * 信誉得分
     */
    @ApiModelProperty("信誉得分")
    private String reputationScore;

    /**
     * 恶意文件值
     */
    @ApiModelProperty("恶意文件值")
    private String reputationValue;

    /**
     * 源头数据来源
     */
    @ApiModelProperty("源头数据来源")
    private String sourceRef;

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Date getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(Date evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(Integer informationSource) {
        this.informationSource = informationSource;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getMaliciousName() {
        return maliciousName;
    }

    public void setMaliciousName(String maliciousName) {
        this.maliciousName = maliciousName;
    }

    public String getReputationCategory() {
        return reputationCategory;
    }

    public void setReputationCategory(String reputationCategory) {
        this.reputationCategory = reputationCategory;
    }

    public String getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(String reputationScore) {
        this.reputationScore = reputationScore;
    }

    public String getReputationValue() {
        return reputationValue;
    }

    public void setReputationValue(String reputationValue) {
        this.reputationValue = reputationValue;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }
}