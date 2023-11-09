package com.vrv.vap.amonitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.amonitor.model.PageModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-08-30
 */
@Data
@ToString
@ApiModel(value = "AssetCanvasInfo对象", description = "")
public class AssetCanvasInfo extends PageModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "画布名称")
    private String canvasName;

    @ApiModelProperty(value = "是否置顶:1=是,0=否")
    private Integer canvasTop;

    @ApiModelProperty(value = "画布背景颜色")
    private String canvasBgColor;

    @ApiModelProperty(value = "画布背景图片")
    @TableField("canvas_bg_Img")
    private String canvasBgImg;

    @ApiModelProperty(value = "标签,说明")
    private String label;

    @ApiModelProperty(value = "画布json")
    private String canvasJson;

    @ApiModelProperty(value = "添加时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

}
