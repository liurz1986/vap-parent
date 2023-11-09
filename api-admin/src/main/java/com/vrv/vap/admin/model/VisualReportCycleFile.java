package com.vrv.vap.admin.model;

import javax.persistence.*;
import java.util.Date;

@Table(name = "visual_report_cycle_file")
public class VisualReportCycleFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 周期ID
     */
    @Column(name = "cycle_id")
    private Integer cycleId;

    /**
     * 周期名称
     */
    @Column(name = "cycle_title")
    private String cycleTitle;

    /**
     * 报表ID
     */
    @Column(name = "report_id")
    private Integer reportId;

    /**
     * 报表名称
     */
    @Column(name = "report_title")
    private String reportTitle;

    /**
     * 生成报表名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 生成报表地址
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * 生成时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 0 生成失败 1生成中 2已生成
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 报表生成器生成得ID
     */
    @Column(name = "file_id")
    private String fileId;


    /**
     * 生成类型 0 周期报表  1 手工生成
     */
    @Column(name = "source_type")
    private Integer sourceType;

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
     * 获取周期ID
     *
     * @return cycle_id - 周期ID
     */
    public Integer getCycleId() {
        return cycleId;
    }

    /**
     * 设置周期ID
     *
     * @param cycleId 周期ID
     */
    public void setCycleId(Integer cycleId) {
        this.cycleId = cycleId;
    }

    /**
     * 获取周期名称
     *
     * @return cycle_title - 周期名称
     */
    public String getCycleTitle() {
        return cycleTitle;
    }

    /**
     * 设置周期名称
     *
     * @param cycleTitle 周期名称
     */
    public void setCycleTitle(String cycleTitle) {
        this.cycleTitle = cycleTitle;
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
     * 获取报表名称
     *
     * @return report_title - 报表名称
     */
    public String getReportTitle() {
        return reportTitle;
    }

    /**
     * 设置报表名称
     *
     * @param reportTitle 报表名称
     */
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    /**
     * 获取生成报表名称
     *
     * @return file_name - 生成报表名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置生成报表名称
     *
     * @param fileName 生成报表名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取生成报表地址
     *
     * @return file_path - 生成报表地址
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置生成报表地址
     *
     * @param filePath 生成报表地址
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取生成时间
     *
     * @return create_time - 生成时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置生成时间
     *
     * @param createTime 生成时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}