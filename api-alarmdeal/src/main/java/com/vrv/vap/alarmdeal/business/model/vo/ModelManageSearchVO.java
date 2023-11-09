package com.vrv.vap.alarmdeal.business.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 模型查询VO
 */
@Data
public class ModelManageSearchVO extends ModelManage {
    private String modelFileGuid; // 导入模型文件的唯一标识,主要是为找到对应模型jar包
    private String type; //1:模型配置管理页面，2：已发布模型管理页面，all：所有
    @ApiModelProperty(value="查询状态")
    private String modelStatus; //   all(全部)、1(待测试)、2(已测试)、启动中(4)、停用中(5)、6(已下架)
    @ApiModelProperty(value="创建时间(起始)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTimeStart; // 创建时间(起始)
    @ApiModelProperty(value="创建时间(截止)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTimeEnd; // 创建时间(截止)
    @ApiModelProperty(value="排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value="起始页")
    private Integer start_;//起始页
    @ApiModelProperty(value="每页行数")
    private Integer count_; //每页行数
}
