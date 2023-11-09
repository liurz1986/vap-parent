package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="OltRangeAnalysis对象", description="")
public class OltRangeAnalysisQuery extends Query {

    @ApiModelProperty(value = "主键 主键/NOT NULL/自增长")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务编号")
    private String taskId;

    private String areaCode;

    @ApiModelProperty(value = "区域")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String areaName;

    @ApiModelProperty(value = "警种编码")
    private String policeTypeCode;

    @ApiModelProperty(value = "警种名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String policeTypeName;

    @ApiModelProperty(value = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    @ApiModelProperty(value = "组织机构")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String organ;

    @ApiModelProperty(value = "访问人数")
    private Integer personCount;

    @ApiModelProperty(value = "查询次数")
    private Integer searchCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getPoliceTypeCode() {
        return policeTypeCode;
    }

    public void setPoliceTypeCode(String policeTypeCode) {
        this.policeTypeCode = policeTypeCode;
    }
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }
    public Integer getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public String toString() {
        return "OltRangeAnalysis{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", policeTypeCode=" + policeTypeCode +
            ", policeTypeName=" + policeTypeName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", organ=" + organ +
            ", personCount=" + personCount +
            ", searchCount=" + searchCount +
        "}";
    }
}
