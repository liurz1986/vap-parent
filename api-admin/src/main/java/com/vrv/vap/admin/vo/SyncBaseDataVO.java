package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2020/5/19
 * @description
 */
public class SyncBaseDataVO {

    /**
     * person       人员
     * org          组织机构
     */
    @ApiModelProperty("数据类型")
    private String dataType;

    /**
     * 操作数据
     */
    @ApiModelProperty("操作数据")
    private Map data;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
