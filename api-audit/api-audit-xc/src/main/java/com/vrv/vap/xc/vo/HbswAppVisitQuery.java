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
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="HbswAppVisit对象", description="")
public class HbswAppVisitQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String ip;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "各系统访问详情")
    private String sysDetail;

    @ApiModelProperty(value = "访问总量")
    private Integer total;

    @ApiModelProperty(value = "类型，1-上班访问预警，2-下班访问预警")
    private Integer warnType;

    @ApiModelProperty(value = "预警时间")
    private Date warnTime;

    @ApiModelProperty(value = "地区")
    private String areaCode;

    private String areaName;

    private String organ;

    @ApiModelProperty(value = "数据时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String dataTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getSysDetail() {
        return sysDetail;
    }

    public void setSysDetail(String sysDetail) {
        this.sysDetail = sysDetail;
    }
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    public Integer getWarnType() {
        return warnType;
    }

    public void setWarnType(Integer warnType) {
        this.warnType = warnType;
    }
    public Date getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(Date warnTime) {
        this.warnTime = warnTime;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "HbswAppVisit{" +
            "id=" + id +
            ", ip=" + ip +
            ", userName=" + userName +
            ", sysDetail=" + sysDetail +
            ", total=" + total +
            ", warnType=" + warnType +
            ", warnTime=" + warnTime +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", organ=" + organ +
            ", dataTime=" + dataTime +
        "}";
    }
}
