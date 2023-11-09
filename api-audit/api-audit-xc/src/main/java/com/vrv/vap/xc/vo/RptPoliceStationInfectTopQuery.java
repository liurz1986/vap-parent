package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 每日感染终端数警务站TOP
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="RptPoliceStationInfectTop对象", description="每日感染终端数警务站TOP")
public class RptPoliceStationInfectTopQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "警务站")
    private String policeStation;

    @ApiModelProperty(value = "警务站所属地州")
    private String cityMargin;

    @ApiModelProperty(value = "被感染终端数")
    private Integer count;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

    @ApiModelProperty(value = "记录时间")
    private Date recordTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getPoliceStation() {
        return policeStation;
    }

    public void setPoliceStation(String policeStation) {
        this.policeStation = policeStation;
    }
    public String getCityMargin() {
        return cityMargin;
    }

    public void setCityMargin(String cityMargin) {
        this.cityMargin = cityMargin;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        return "RptPoliceStationInfectTop{" +
            "id=" + id +
            ", policeStation=" + policeStation +
            ", cityMargin=" + cityMargin +
            ", count=" + count +
            ", dataTime=" + dataTime +
            ", recordTime=" + recordTime +
        "}";
    }
}
