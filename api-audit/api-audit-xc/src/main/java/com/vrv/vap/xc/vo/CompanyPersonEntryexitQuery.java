package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业人员出入境记录
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPersonEntryexit对象", description="企业人员出入境记录")
public class CompanyPersonEntryexitQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "人员id")
    private Integer personId;

    @ApiModelProperty(value = "出境时间")
    private String outTime;

    @ApiModelProperty(value = "入境时间")
    private String inTime;

    @ApiModelProperty(value = "目的城市")
    private String record;

    @ApiModelProperty(value = "出入境原因")
    private String reason;

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
    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }
    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
        return "CompanyPersonEntryexit{" +
            "id=" + id +
            ", personId=" + personId +
            ", outTime=" + outTime +
            ", inTime=" + inTime +
            ", record=" + record +
            ", reason=" + reason +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
