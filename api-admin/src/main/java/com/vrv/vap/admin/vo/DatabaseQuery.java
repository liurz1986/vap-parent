package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import com.vrv.vap.admin.common.ConvertField;
import io.swagger.annotations.ApiModelProperty;

public class DatabaseQuery extends Query {

    @ApiModelProperty("搜索语句")
    private String queryJsonStr;

    private Integer id;

    private String topList;

    private String[] param;

    /**
     * 结果需要的字段
     */
    @ConvertField
    private String[] resultFields;

    public String getQueryJsonStr() {
        return queryJsonStr;
    }

    public void setQueryJsonStr(String queryJsonStr) {
        this.queryJsonStr = queryJsonStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopList() {
        return topList;
    }

    public void setTopList(String topList) {
        this.topList = topList;
    }

    public String[] getResultFields() {
        return resultFields;
    }

    public void setResultFields(String[] resultFields) {
        this.resultFields = resultFields;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}
