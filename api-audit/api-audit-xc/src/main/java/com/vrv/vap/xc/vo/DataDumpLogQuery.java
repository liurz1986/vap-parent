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
 * 数据备份记录
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="DataDumpLog对象", description="数据备份记录")
public class DataDumpLogQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "策略编号")
    private Integer strategyId;

    @ApiModelProperty(value = "数据类型，1-es 2-mysql 3-hive 4-hbase")
    private Integer dataType;

    @ApiModelProperty(value = "数据标识")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String dataId;

    @ApiModelProperty(value = "数据描述")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String dataDesc;

    @ApiModelProperty(value = "具体数据")
    private String dataDetail;

    @ApiModelProperty(value = "快照标识")
    private String snapshotName;

    @ApiModelProperty(value = "备份时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private Date dumpTime;

    @ApiModelProperty(value = "备份文件路径")
    private String dumpFilePath;

    @ApiModelProperty(value = "备份文件状态，1-存在，0-已删除")
    private Integer dumpFileState;

    @ApiModelProperty(value = "备份文件MD5值")
    private String dumpFileMd5;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
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
    public String getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }
    public Date getDumpTime() {
        return dumpTime;
    }

    public void setDumpTime(Date dumpTime) {
        this.dumpTime = dumpTime;
    }
    public String getDumpFilePath() {
        return dumpFilePath;
    }

    public void setDumpFilePath(String dumpFilePath) {
        this.dumpFilePath = dumpFilePath;
    }
    public Integer getDumpFileState() {
        return dumpFileState;
    }

    public void setDumpFileState(Integer dumpFileState) {
        this.dumpFileState = dumpFileState;
    }
    public String getDumpFileMd5() {
        return dumpFileMd5;
    }

    public void setDumpFileMd5(String dumpFileMd5) {
        this.dumpFileMd5 = dumpFileMd5;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "DataDumpLog{" +
            "id=" + id +
            ", strategyId=" + strategyId +
            ", dataType=" + dataType +
            ", dataId=" + dataId +
            ", dataDesc=" + dataDesc +
            ", dataDetail=" + dataDetail +
            ", snapshotName=" + snapshotName +
            ", dumpTime=" + dumpTime +
            ", dumpFilePath=" + dumpFilePath +
            ", dumpFileState=" + dumpFileState +
            ", dumpFileMd5=" + dumpFileMd5 +
            ", remark=" + remark +
        "}";
    }
}
