package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value="OltBevisitedPeopleArea对象", description="")
public class OltBevisitedPeopleAreaQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "警种")
    private String policeType;

    @ApiModelProperty(value = "查询的地区个数")
    private Integer nums;

    @ApiModelProperty(value = "查询次数")
    private Integer total;

    private String areaCode;

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
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "OltBevisitedPeopleArea{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", policeType=" + policeType +
            ", nums=" + nums +
            ", total=" + total +
            ", areaCode=" + areaCode +
        "}";
    }
}
