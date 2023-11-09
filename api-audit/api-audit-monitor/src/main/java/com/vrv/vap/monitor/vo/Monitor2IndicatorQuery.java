package com.vrv.vap.monitor.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.monitor.model.PageModel;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * <p>
 * V2-指标Query
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-10-26
 */
@Data
@ToString
@ApiModel(value = "Monitor2Indicator Query对象", description = "V2-指标Query")
public class Monitor2IndicatorQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    @ApiModelProperty(value = "指标名称(内存占比/cpu利用率/磁盘等)")
    private String indicatorName;

    @ApiModelProperty(value = "指标分类")
    private String indicatorType;

    @ApiModelProperty(value = "指标标识/存储字段")
    private String indicatorField;

    @ApiModelProperty(value = "是否可用")
    private Integer available;


}
