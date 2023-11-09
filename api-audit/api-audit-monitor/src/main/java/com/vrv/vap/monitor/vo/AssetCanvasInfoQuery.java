package com.vrv.vap.monitor.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(value="AssetCanvasInfo对象", description="")
public class AssetCanvasInfoQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "画布名称")
    private String canvasName;

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
