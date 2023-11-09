package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lilang
 * @date 2019/11/20
 * @description
 */
@Data
public class DomainQuery extends Query {


    @ApiModelProperty(value = "关联使用guid")
    private String code;

    @ApiModelProperty(value = "区域名称")
    @QueryLike
    private String domainName;
    @ApiModelProperty(value = "区域信息")
    private String domainInfo;

    @ApiModelProperty(value = "上级编号")
    private String parentCode;

    private String ip;
    private String secretLevel;
    @ApiModelProperty(value="窃泄密值")
    private String beginValue;
    @ApiModelProperty(value="窃泄密值")
    private String endValue;
    @ApiModelProperty(value = "组织机构名称")
    private String orgName;
    @ApiModelProperty(value = "责任人")
    private String responsibleName;
    private List<String> codes;
    private Boolean isJustAssetOfConcern;
    private Integer userId;
    private List<String> ids;


}
