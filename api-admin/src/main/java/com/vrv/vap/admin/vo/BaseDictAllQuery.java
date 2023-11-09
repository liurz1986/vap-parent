package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("basedictall组织机构分页查询参数")
@Data
public class BaseDictAllQuery extends Query {
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("字典编码")
    private String code;

    @ApiModelProperty("字典值")
    @QueryLike
    private String codeValue;

    @ApiModelProperty("字典唯一编码")
    private String type;

    @ApiModelProperty("字典父编码")
    private String parentType;

    @ApiModelProperty("是否叶子节点 0 否  1是")
    private String leaf;

    @ApiModelProperty("字典描述")
    @QueryLike
    private String description;


    private String createId;

    private String sort;
}
