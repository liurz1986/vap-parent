package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="RptOrganAppQuery对象", description="")
public class RptOrganAppQuery2 extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String userId;

    private String userName;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String orgCode;

    private String orgName;

    private Integer sysNum;

    private Integer peopleNum;

    private Integer carNum;

    private Integer visitCount;

    private String cronTime;

    private String recordTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public Integer getSysNum() {
        return sysNum;
    }

    public void setSysNum(Integer sysNum) {
        this.sysNum = sysNum;
    }
    public Integer getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(Integer peopleNum) {
        this.peopleNum = peopleNum;
    }
    public Integer getCarNum() {
        return carNum;
    }

    public void setCarNum(Integer carNum) {
        this.carNum = carNum;
    }
    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
    public String getCronTime() {
        return cronTime;
    }

    public void setCronTime(String cronTime) {
        this.cronTime = cronTime;
    }
    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "RptOrganAppQuery{" +
            "id=" + id +
            ", userId=" + userId +
            ", userName=" + userName +
            ", orgCode=" + orgCode +
            ", orgName=" + orgName +
            ", sysNum=" + sysNum +
            ", peopleNum=" + peopleNum +
            ", carNum=" + carNum +
            ", visitCount=" + visitCount +
            ", cronTime=" + cronTime +
            ", recordTime=" + recordTime +
        "}";
    }
}
