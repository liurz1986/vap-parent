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
@ApiModel(value="OltMorePeopleVisited对象", description="")
public class OltMorePeopleVisitedQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "查询系统数")
    private Integer sysNum;

    @ApiModelProperty(value = "查询人数")
    private Integer peopleNum;

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
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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

    @Override
    public String toString() {
        return "OltMorePeopleVisited{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", idCard=" + idCard +
            ", sysNum=" + sysNum +
            ", peopleNum=" + peopleNum +
        "}";
    }
}
