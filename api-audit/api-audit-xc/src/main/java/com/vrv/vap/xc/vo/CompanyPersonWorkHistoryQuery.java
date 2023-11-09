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
 * 企业人员工作履历
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPersonWorkHistory对象", description="企业人员工作履历")
public class CompanyPersonWorkHistoryQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "人员id")
    private Integer personId;

    @ApiModelProperty(value = "标题")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String title;

    @ApiModelProperty(value = "企业名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "岗位名称")
    private String position;

    @ApiModelProperty(value = "是否签订劳动合同")
    private String signedWorkerContract;

    @ApiModelProperty(value = "劳动合同起止时间")
    private String workerContractPeriod;

    @ApiModelProperty(value = "上传扫描件")
    private String workerContractFile;

    @ApiModelProperty(value = "备注")
    private String remark;

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
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getSignedWorkerContract() {
        return signedWorkerContract;
    }

    public void setSignedWorkerContract(String signedWorkerContract) {
        this.signedWorkerContract = signedWorkerContract;
    }
    public String getWorkerContractPeriod() {
        return workerContractPeriod;
    }

    public void setWorkerContractPeriod(String workerContractPeriod) {
        this.workerContractPeriod = workerContractPeriod;
    }
    public String getWorkerContractFile() {
        return workerContractFile;
    }

    public void setWorkerContractFile(String workerContractFile) {
        this.workerContractFile = workerContractFile;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        return "CompanyPersonWorkHistory{" +
            "id=" + id +
            ", personId=" + personId +
            ", title=" + title +
            ", name=" + name +
            ", position=" + position +
            ", signedWorkerContract=" + signedWorkerContract +
            ", workerContractPeriod=" + workerContractPeriod +
            ", workerContractFile=" + workerContractFile +
            ", remark=" + remark +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
