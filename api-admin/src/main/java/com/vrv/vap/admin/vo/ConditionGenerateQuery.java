package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2019/7/11
 * @description
 */
public class ConditionGenerateQuery extends Query {

    @ApiModelProperty("索引")
    public String index;

    @ApiModelProperty("开始时间")
    public String startTime;

    @ApiModelProperty("结束时间")
    public String endTime;

    @ApiModelProperty("时间字段")
    public String timeFieldName;

    @ApiModelProperty("搜索语句")
    public String queryStr;

    @ApiModelProperty("安全域字段")
    public String domainFieldName;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getDomainFieldName() {
        return domainFieldName;
    }

    public void setDomainFieldName(String domainFieldName) {
        this.domainFieldName = domainFieldName;
    }
}
