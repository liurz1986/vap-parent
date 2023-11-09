package com.vrv.vap.alarmdeal.business.asset.datasync.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 查询差异数据详细
 */
@Data
public class AssetBookDiffDetailVO extends AssetBookDiffColumnsVO{
    @ApiModelProperty(value="部门Code")
    private String orgCode; // 组织机构名称(单位、部门)
    @ApiModelProperty(value="责任人code")
    private String responsibleCode; // 责任人code
    private String guid;// 异常记录id

    private String syncSource; // 数据源 ；其中syncSource值为asset_source的表示正式库，不用与字典表比对翻译。
}
