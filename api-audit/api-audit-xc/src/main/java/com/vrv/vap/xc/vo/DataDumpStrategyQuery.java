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
 * 数据备份策略
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="DataDumpStrategy对象", description="数据备份策略")
public class DataDumpStrategyQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "数据类型，1-es 2-mysql 3-hive 4-hbase")
    private Integer dataType;

    @ApiModelProperty(value = "数据标识")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String dataId;

    @ApiModelProperty(value = "数据描述")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String dataDesc;

    @ApiModelProperty(value = "数据保存时间，单位：天")
    private Integer saveTime;

    @ApiModelProperty(value = "清理模式，1-只清理不备份，2-先备份后清理，3-先备份后清理再转存")
    private Integer type;

    @ApiModelProperty(value = "转储方式，1-手动下载，2-sftp，3-共享目录")
    private Integer dumpMode;

    @ApiModelProperty(value = "状态，1-启用，0-关闭")
    private Integer state;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
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
    public Integer getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(Integer saveTime) {
        this.saveTime = saveTime;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public Integer getDumpMode() {
        return dumpMode;
    }

    public void setDumpMode(Integer dumpMode) {
        this.dumpMode = dumpMode;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "DataDumpStrategy{" +
            "id=" + id +
            ", dataType=" + dataType +
            ", dataId=" + dataId +
            ", dataDesc=" + dataDesc +
            ", saveTime=" + saveTime +
            ", type=" + type +
            ", dumpMode=" + dumpMode +
            ", state=" + state +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", remark=" + remark +
        "}";
    }
}
