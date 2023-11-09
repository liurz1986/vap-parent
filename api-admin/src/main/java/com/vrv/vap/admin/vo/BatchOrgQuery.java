package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("批量查询机构")
public class BatchOrgQuery {

    @ApiModelProperty("机构编码列表，用逗号分开")
    private String codes;

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }
}
