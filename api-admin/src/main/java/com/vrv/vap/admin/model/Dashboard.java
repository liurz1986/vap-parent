package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "visual_dashboard")
@ApiModel("仪表盘对象")
public class Dashboard {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    @Column(name = "title")
    @ApiModelProperty("标题")
    private String title;

    @Column(name = "description")
    @ApiModelProperty("描述")
    private String description;

    @Column(name = "ui_state_json")
    @ApiModelProperty("UI状态")
    private String uiStateJSON;

    @Column(name = "time_restore")
    @ApiModelProperty("时间存储")
    private String timeRestore;

    @Column(name = "image_id")
    @ApiModelProperty("图片ID")
    private Integer imageId;

    @Column(name = "top")
    @ApiModelProperty("置顶")
    private Integer top;

    @Column(name = "thumbnail_path")
    @ApiModelProperty("仪表板封面")
    private String thumbnailPath;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUiStateJSON() {
        return uiStateJSON;
    }

    public void setUiStateJSON(String uiStateJSON) {
        this.uiStateJSON = uiStateJSON;
    }

    public String getTimeRestore() {
        return timeRestore;
    }

    public void setTimeRestore(String timeRestore) {
        this.timeRestore = timeRestore;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
