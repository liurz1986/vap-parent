package com.vrv.vap.amonitor.model.es;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.amonitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table data_clean_log
 *
 * @mbg.generated do_not_delete_during_merge 2021-01-07 16:44:49
 */
@ApiModel
@SuppressWarnings("unused")
public class DataCleanLog extends PageModel {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 数据类型，1-es 2-mysql 3-hive 4-hbase
     */
    @ApiModelProperty("数据类型，1-es 2-mysql 3-hive 4-hbase")
    private Integer dataType;

    /**
     * 数据标识
     */
    @ApiModelProperty("数据标识")
    private String dataId;

    /**
     * 数据描述
     */
    @ApiModelProperty("数据描述")
    private String dataDesc;

    /**
     * 具体数据
     */
    @ApiModelProperty("具体数据")
    private String dataDetail;

    /**
     * 清理时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("清理时间")
    private Date cleanTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataDetail() {
        return dataDetail;
    }

    public void setDataDetail(String dataDetail) {
        this.dataDetail = dataDetail;
    }

    public Date getCleanTime() {
        return cleanTime;
    }

    public void setCleanTime(Date cleanTime) {
        this.cleanTime = cleanTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}