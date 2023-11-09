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
 * 数据清理记录
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="DataCleanLog对象", description="数据清理记录")
public class DataCleanLogQuery extends Query {

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

    @ApiModelProperty(value = "具体数据")
    private String dataDetail;

    @ApiModelProperty(value = "清理时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private Date cleanTime;

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

    @Override
    public String toString() {
        return "DataCleanLog{" +
            "id=" + id +
            ", dataType=" + dataType +
            ", dataId=" + dataId +
            ", dataDesc=" + dataDesc +
            ", dataDetail=" + dataDetail +
            ", cleanTime=" + cleanTime +
            ", remark=" + remark +
        "}";
    }
}
