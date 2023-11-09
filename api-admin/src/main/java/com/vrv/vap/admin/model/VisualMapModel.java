package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "visual_map")
@ApiModel("地图对象")
public class VisualMapModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;


    /**
     * 索引名称
     */
    @Column(name = "map_json")
    @ApiModelProperty("索引名称")
    private String mapJson;

    /**
     * 索引类型
     */
    @Column(name = "map_default")
    @ApiModelProperty("索引类型")
    private String mapDefault;

    /**
     * 最后修改时间
     */
    @Column(name = "last_update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @ApiModelProperty("最后修改时间")
    private Date lastUpdateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMapJson() {
        return mapJson;
    }

    public void setMapJson(String mapJson) {
        this.mapJson = mapJson;
    }

    public String getMapDefault() {
        return mapDefault;
    }

    public void setMapDefault(String mapDefault) {
        this.mapDefault = mapDefault;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
