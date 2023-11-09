package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "visual_report_model")
public class VisualReportModel {
    /**
     * 模板ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 模板标题
     */
    private String title;

    /**
     * 副标题
     */
    @Column(name = "secondary_title")
    private String secondaryTitle;

    /**
     * ftl模板标识
     */
    @Column(name = "template_name")
    private String templateName;

    /**
     * 数据标识
     */
    @Column(name = "data_id")
    private String dataId;

    /**
     * ftl模板类型，1-图表，2-文本，3-表格
     */
    @Column(name = "template_type")
    private Integer templateType;

    /**
     * group
     */
    @Column(name = "group_id")
    private String groupId;

    /**
     * 模板顺序
     */
    @Column(name = "template_order")
    private Integer templateOrder;


    /**
     * 汇总描述
     */
    @Column(name = "template_desc")
    private String templateDesc;

    /**
     * 获取模板ID
     *
     * @return id - 模板ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置模板ID
     *
     * @param id 模板ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取模板标题
     *
     * @return title - 模板标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置模板标题
     *
     * @param title 模板标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取ftl模板标识
     *
     * @return template_name - ftl模板标识
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * 设置ftl模板标识
     *
     * @param templateName ftl模板标识
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 获取数据标识
     *
     * @return data_id - 数据标识
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * 设置数据标识
     *
     * @param dataId 数据标识
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * 获取ftl模板类型，1-图表，2-文本，3-表格
     *
     * @return template_type - ftl模板类型，1-图表，2-文本，3-表格
     */
    public Integer getTemplateType() {
        return templateType;
    }

    /**
     * 设置ftl模板类型，1-图表，2-文本，3-表格
     *
     * @param templateType ftl模板类型，1-图表，2-文本，3-表格
     */
    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getTemplateOrder() {
        return templateOrder;
    }

    public void setTemplateOrder(Integer templateOrder) {
        this.templateOrder = templateOrder;
    }

    public String getTemplateDesc() {
        return templateDesc;
    }

    public void setTemplateDesc(String templateDesc) {
        this.templateDesc = templateDesc;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }
}