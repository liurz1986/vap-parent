package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2022/5/18
 * @description
 */
public class SyncBaseDataQuery extends Query {
    @ApiModelProperty("数据来源")
    private String source;
    @ApiModelProperty("任务类型")
    private String type;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
