package com.vrv.vap.data.model;

import javax.persistence.*;

@Table(name = "data_report_theme")
public class ReportTheme {
    /**
     * 主题ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 主题名称
     */
    private String name;

    /**
     * 大小
     */
    private String size;

    /**
     * 边距, mm
     */
    private Byte margin;

    /**
     * 背景图片
     */
    private String background;

    /**
     * Logo
     */
    private String logo;

    /**
     * 标题样式
     */
    private String title;

    /**
     * 副标题样式
     */
    @Column(name = "sub_title")
    private String subTitle;

    /**
     * 目录样式
     */
    private String topic;

    /**
     * 水印
     */
    private String sign;

    /**
     * 页头
     */
    private String header;

    /**
     * 页尾
     */
    private String footer;

    /**
     * 获取主题ID
     *
     * @return id - 主题ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主题ID
     *
     * @param id 主题ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取主题名称
     *
     * @return name - 主题名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置主题名称
     *
     * @param name 主题名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取大小
     *
     * @return size - 大小
     */
    public String getSize() {
        return size;
    }

    /**
     * 设置大小
     *
     * @param size 大小
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 获取边距, mm
     *
     * @return margin - 边距, mm
     */
    public Byte getMargin() {
        return margin;
    }

    /**
     * 设置边距, mm
     *
     * @param margin 边距, mm
     */
    public void setMargin(Byte margin) {
        this.margin = margin;
    }

    /**
     * 获取背景图片
     *
     * @return background - 背景图片
     */
    public String getBackground() {
        return background;
    }

    /**
     * 设置背景图片
     *
     * @param background 背景图片
     */
    public void setBackground(String background) {
        this.background = background;
    }

    /**
     * 获取Logo
     *
     * @return logo - Logo
     */
    public String getLogo() {
        return logo;
    }

    /**
     * 设置Logo
     *
     * @param logo Logo
     */
    public void setLogo(String logo) {
        this.logo = logo;
    }

    /**
     * 获取标题样式
     *
     * @return title - 标题样式
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题样式
     *
     * @param title 标题样式
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取副标题样式
     *
     * @return sub_title - 副标题样式
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * 设置副标题样式
     *
     * @param subTitle 副标题样式
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * 获取目录样式
     *
     * @return topic - 目录样式
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 设置目录样式
     *
     * @param topic 目录样式
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 获取水印
     *
     * @return sign - 水印
     */
    public String getSign() {
        return sign;
    }

    /**
     * 设置水印
     *
     * @param sign 水印
     */
    public void setSign(String sign) {
        this.sign = sign;
    }

    /**
     * 获取页头
     *
     * @return header - 页头
     */
    public String getHeader() {
        return header;
    }

    /**
     * 设置页头
     *
     * @param header 页头
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * 获取页尾
     *
     * @return footer - 页尾
     */
    public String getFooter() {
        return footer;
    }

    /**
     * 设置页尾
     *
     * @param footer 页尾
     */
    public void setFooter(String footer) {
        this.footer = footer;
    }
}