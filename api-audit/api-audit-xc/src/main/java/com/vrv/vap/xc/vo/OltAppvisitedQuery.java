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
@ApiModel(value="OltAppvisited对象", description="")
public class OltAppvisitedQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "警种名称")
    private String policeTypeName;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "机构名称")
    private String organ;

    @ApiModelProperty(value = "查询次数")
    private Integer count;

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
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
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
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "OltAppvisited{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", policeTypeName=" + policeTypeName +
            ", userId=" + userId +
            ", userName=" + userName +
            ", organ=" + organ +
            ", count=" + count +
            ", areaCode=" + areaCode +
        "}";
    }
}
