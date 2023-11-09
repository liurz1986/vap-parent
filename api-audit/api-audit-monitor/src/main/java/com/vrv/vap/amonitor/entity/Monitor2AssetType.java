package com.vrv.vap.amonitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.amonitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@ApiModel(value = "Monitor2AssetType对象", description = "V2-指标算法分类")
public class Monitor2AssetType extends PageModel {

    @TableId(value = "guid", type = IdType.ASSIGN_UUID)
    private String guid;

    @ApiModelProperty(value = "uniqueCode")
    private String uniqueCode;


    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "treeCode")
    private String treeCode;

    @ApiModelProperty(value = "iconCls")
    private String iconCls;

    @ApiModelProperty(value = "parentTreeCode")
    private String parentTreeCode;


    @ApiModelProperty(value = "type_level")
    private Integer typeLevel;

}
