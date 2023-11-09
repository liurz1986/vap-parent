package com.vrv.vap.alarmdeal.business.analysis.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 预警管理VO
 * @author wd-pc
 *
 */
@Data
@ApiModel("预警管理对象")
public class WarnManagerVO {
	@ApiModelProperty(value="使用者用户Id")
    private String userId;
	@ApiModelProperty(value="使用者用户名称")
    private String userName;
	@ApiModelProperty(value="预警管理guid")
	private String guid ;
	@ApiModelProperty(value="预警管理名称")
	private String warnName ;
	@ApiModelProperty(value="预警父类名称")
	private String warnSuperType;
	@ApiModelProperty(value="预警类型")
	private String warnType;
	@ApiModelProperty(value="预警等级")
	private String warnLevel;
	@ApiModelProperty(value="预警状态")
	private String warnStatus;
	@ApiModelProperty(value="预警创建时间")
	private String createTime;
	@ApiModelProperty(value="预警创建者")
	private String creater;
	@ApiModelProperty(value="预警范围")
	private String warn_range;
	@ApiModelProperty(value="预警文件路径")
	private String file;
	@ApiModelProperty(value="预警发布人")
	private String publishedPerson;
	@ApiModelProperty(value="预警发布时间")
	private String publishedTime;
	@ApiModelProperty(value="预警来源")
	private String comeFrom;
	@ApiModelProperty(value="预警详情")
	private String warnDetail;
	@ApiModelProperty(value="预警可能产生结果")
	private String possibleResult;
	@ApiModelProperty(value="预警解决方案")
	private String solution;
	@ApiModelProperty(value="预警类型")
	private String type;
	@ApiModelProperty(value="预警等级名称")
	private String warnLevelName;
	@ApiModelProperty(value="预警类型名称")
	private String warnTypeName ;
	@ApiModelProperty(value="预警状态名称")
	private String warnStatusName;
	@ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
	@ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
	@ApiModelProperty(value="开始行")
	private Integer start_;//
	@ApiModelProperty(value="每页行数")
	private Integer count_;
}
