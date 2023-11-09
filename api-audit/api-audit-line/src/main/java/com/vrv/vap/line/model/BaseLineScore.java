package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 动态基线表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@ApiModel(value="BaseLineScore对象", description="访问序列得分表")
public class BaseLineScore {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer frequentId;

    private float similarityScore;

    private float similarityScoreOrg;

    private float similarityScoreRole;

    private float hourScore;

    private float packgeScore;

    private String compress;

    private String userKey;

    private String type;

    private String startTime;

    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(value = "入库时间")
    private String time;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFrequentId() {
        return frequentId;
    }

    public void setFrequentId(Integer frequentId) {
        this.frequentId = frequentId;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(float similarityScore) {
        this.similarityScore = similarityScore;
    }

    public float getHourScore() {
        return hourScore;
    }

    public void setHourScore(float hourScore) {
        this.hourScore = hourScore;
    }

    public float getPackgeScore() {
        return packgeScore;
    }

    public void setPackgeScore(float packgeScore) {
        this.packgeScore = packgeScore;
    }

    public String getCompress() {
        return compress;
    }

    public void setCompress(String compress) {
        this.compress = compress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String sysId;

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }


    public float getSimilarityScoreOrg() {
        return similarityScoreOrg;
    }

    public void setSimilarityScoreOrg(float similarityScoreOrg) {
        this.similarityScoreOrg = similarityScoreOrg;
    }

    public float getSimilarityScoreRole() {
        return similarityScoreRole;
    }

    public void setSimilarityScoreRole(float similarityScoreRole) {
        this.similarityScoreRole = similarityScoreRole;
    }
}
