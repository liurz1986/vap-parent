package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 动态基线表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@ApiModel(value="JUserLogs", description="访问序列得分表")
public class JUserLogs {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private long size;
    private long queryTime;
    private long splitTime;
    private long analysisTime;
    private long filterTime;
    private long saveTime;
    private String userKey;
    private String type;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public long getSplitTime() {
        return splitTime;
    }

    public void setSplitTime(long splitTime) {
        this.splitTime = splitTime;
    }

    public long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public long getFilterTime() {
        return filterTime;
    }

    public void setFilterTime(long filterTime) {
        this.filterTime = filterTime;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
