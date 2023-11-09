package com.vrv.vap.alarmdeal.business.analysis.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ObjectResourceVO {
    @ApiModelProperty(value="guid")
    private String guid;

    @ApiModelProperty(value="名称")
    private  String name;
    @ApiModelProperty(value="标题")
    private  String title;

    @ApiModelProperty(value="内容")
    private  String content;

    @ApiModelProperty(value="描述")
    private  String remark;

    @ApiModelProperty(value="类型")
    private  String objectResourceType;
    

    @ApiModelProperty(value="排序字段")

    private String order_;
    @ApiModelProperty(value="排序顺序")

    private String by_;
    @ApiModelProperty(value="开始行")

    private Integer start_;
    @ApiModelProperty(value="每页个数")
    private Integer count_;



}
