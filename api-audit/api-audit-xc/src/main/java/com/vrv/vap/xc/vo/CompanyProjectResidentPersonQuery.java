package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业项目的驻场人员
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProjectResidentPerson对象", description="企业项目的驻场人员")
public class CompanyProjectResidentPersonQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "职责")
    private String duty;

    @ApiModelProperty(value = "工作地点")
    private String workSite;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
    public String getWorkSite() {
        return workSite;
    }

    public void setWorkSite(String workSite) {
        this.workSite = workSite;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyProjectResidentPerson{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", name=" + name +
            ", idCard=" + idCard +
            ", duty=" + duty +
            ", workSite=" + workSite +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
