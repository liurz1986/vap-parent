package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2020/8/24
 * @description 报告主题实体类
 */
@Table(name = "visual_report_theme")
@ApiModel("报告主题实体类")
public class ReportTheme {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("主题名称")
    private String name;

    @ApiModelProperty("大小")
    private String size;

    @ApiModelProperty("边距")
    private Integer margin;

    @ApiModelProperty("背景图片")
    @Column(name = "background")
    private String background;

    @ApiModelProperty("图表")
    private String logo;

    @ApiModelProperty("标题样式")
    private String title;

    @ApiModelProperty("副标题样式")
    @Column(name = "subTitle")
    private String subTitle;

    @ApiModelProperty("目录样式")
    @Column(name = "topic")
    private String topic;

    @ApiModelProperty("水印")
    private String sign;

    @ApiModelProperty("页头")
    private String header;

    @ApiModelProperty("页尾")
    private String footer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getMargin() {
        return margin;
    }

    public void setMargin(Integer margin) {
        this.margin = margin;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
}
