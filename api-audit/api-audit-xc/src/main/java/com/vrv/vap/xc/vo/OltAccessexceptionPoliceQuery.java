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
@ApiModel(value="OltAccessexceptionPolice对象", description="")
public class OltAccessexceptionPoliceQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "区域")
    private String areaCode;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String username;

    @ApiModelProperty(value = "警种")
    private String policeType;

    @ApiModelProperty(value = "警种平均查询次数")
    private Integer avgCount;

    @ApiModelProperty(value = "查询次数")
    private Integer count;

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
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public Integer getAvgCount() {
        return avgCount;
    }

    public void setAvgCount(Integer avgCount) {
        this.avgCount = avgCount;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OltAccessexceptionPolice{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", idCard=" + idCard +
            ", username=" + username +
            ", policeType=" + policeType +
            ", avgCount=" + avgCount +
            ", count=" + count +
        "}";
    }
}
