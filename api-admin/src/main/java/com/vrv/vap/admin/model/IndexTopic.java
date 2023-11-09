package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name="discover_topic")
@Setter
@Getter
public class IndexTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("ID")
    private Integer id;

    @Column(name="type")
    @ApiModelProperty("类型")
    private String type;

    @Column(name="name")
    @ApiModelProperty("分组名称")
    private String name;

    @Column(name="parent_id")
    @ApiModelProperty("父组ID，0表示根目录")
    private Integer parentId;

    @Column(name="index_id")
    @ApiModelProperty("索引ID")
    private String indexId;


    @Column(name="filter_json")
    @ApiModelProperty("'索引过滤条件'")
    private String filterJson;


    @Column(name="group_order")
    @ApiModelProperty("顺序")
    private Integer groupOrder;


    @Column(name="group_default")
    @ApiModelProperty("默认索引")
    private Integer groupDefault;


    @Column(name="status")
    @ApiModelProperty("状态")
    private String status;
}


