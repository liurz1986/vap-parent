package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2022/7/21
 * @description
 */
public class SyncBaseDataLogQuery extends Query {

    @ApiModelProperty("任务名称")
    @QueryLike
    private String taskName;
    @ApiModelProperty("任务类型")
    private String type;
    @ApiModelProperty("数据来源")
    private String source;

    public String getType() {
        return type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
