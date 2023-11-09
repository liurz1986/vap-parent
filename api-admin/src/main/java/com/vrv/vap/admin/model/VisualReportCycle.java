package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "visual_report_cycle")
public class VisualReportCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 报表ID
     */
    @Column(name = "report_id")
    private Integer reportId;

    /**
     * 周期名称
     */
    @ApiModelProperty("周期标题")
    private String title;

    /**
     * 0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 
     */
    @ApiModelProperty("类型：0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月")
    private Integer type;

    /**
     * cron 表达式
     */
    @ApiModelProperty("cron 表达式")
    private String cron;

    /**
     * 生成次数
     */
    private Integer count;

    /**
     * 0 停用 1 启用
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 报表参数
     */
    @ApiModelProperty("参数")
    private String param;

    /**
     * 最新执行时间
     */
    @Column(name = "last_time")
    private Date lastTime;

    /**
     * 文件类型 ：pdf 1 doc 2 html 3
     */
    @ApiModelProperty("文件类型")
    private String fileType;
    /**
     * 报表类型，node; java
     */
    @ApiModelProperty(hidden = true)
    private String reportType;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取报表ID
     *
     * @return report_id - 报表ID
     */
    public Integer getReportId() {
        return reportId;
    }

    /**
     * 设置报表ID
     *
     * @param reportId 报表ID
     */
    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    /**
     * 获取周期名称
     *
     * @return title - 周期名称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置周期名称
     *
     * @param title 周期名称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 
     *
     * @return type - 0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 
     *
     * @param type 0-自定义 1-单次 2-每小时 3-每天 4-每周 5-每月 
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取cron 表达式
     *
     * @return cron - cron 表达式
     */
    public String getCron() {
        return cron;
    }

    /**
     * 设置cron 表达式
     *
     * @param cron cron 表达式
     */
    public void setCron(String cron) {
        this.cron = cron;
    }

    /**
     * 获取生成次数
     *
     * @return count - 生成次数
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置生成次数
     *
     * @param count 生成次数
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 获取0 停用 1 启用
     *
     * @return status - 0 停用 1 启用
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置0 停用 1 启用
     *
     * @param status 0 停用 1 启用
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取报表参数
     *
     * @return param - 报表参数
     */
    public String getParam() {
        return param;
    }

    /**
     * 设置报表参数
     *
     * @param param 报表参数
     */
    public void setParam(String param) {
        this.param = param;
    }


    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}