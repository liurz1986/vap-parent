package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table olt_accessexception_organ
 *
 * @mbg.generated do_not_delete_during_merge 2018-05-29 11:43:24
 */
@ApiModel
@SuppressWarnings("unused")
public class OltAccessexceptionOrganQuery extends Query {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private String taskId;

    /**
     * 区域
     */
    @ApiModelProperty("区域")
    private String areaCode;

    /**
     * 身份证
     */
    @ApiModelProperty("身份证")
    private String idCard;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String username;

    /**
     * 机构编码
     */
    @ApiModelProperty("机构编码")
    private String orgCode;

    /**
     * 警种平均查询次数
     */
    @ApiModelProperty("警种平均查询次数")
    private Integer avgCount;

    /**
     * 查询次数
     */
    @ApiModelProperty("查询次数")
    private Integer count;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String orgname;


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

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
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

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }
}