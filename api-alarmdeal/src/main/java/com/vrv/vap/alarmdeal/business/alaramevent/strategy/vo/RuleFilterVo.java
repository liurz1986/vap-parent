package com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventRuleParams;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.ParamConfigVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.StartConfigVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 18:02
 */
@Data
public class RuleFilterVo {
    @ApiModelProperty("主键guid")
    private String guid;

    @ApiModelProperty("规则ID")
    private String ruleId;

    @ApiModelProperty("规则ID")
    private String filterCode;

    @ApiModelProperty("规则参数list")
    private EventRuleParams params;

    @ApiModelProperty("规则名称")
    private String filterName;

    @ApiModelProperty("规则类型")
    private String filterType;

    @ApiModelProperty("是否启用")
    private String isStarted;

    @ApiModelProperty("是否可以配置")
    private String isConfigure;

    @ApiModelProperty("是否配置数据")
    private String isConfigData;
    private String tag;
    private List<ParamConfigVO> paramConfig;
    private StartConfigVO startConfig;
    private Map<String, Object> paramValue;
    private FilterConfigObject filterConfig;
}
