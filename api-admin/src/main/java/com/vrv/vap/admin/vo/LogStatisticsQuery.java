package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("日志统计查询对象")
public class LogStatisticsQuery extends Query {

    @ApiModelProperty("主键")
    private String id;
    @ApiModelProperty("类别")
    private String category;
    @ApiModelProperty("区域名称")
    private String areaName;
    @ApiModelProperty("来源IP")
    private String  sourceIp;
    @ApiModelProperty("存储日期")
    private String  storageDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String area) {
        this.areaName = area;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getStorageDate() {
        return storageDate;
    }

    public void setStorageDate(String storageDate) {
        this.storageDate = storageDate;
    }
}
