package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-19
 */
@ApiModel(value="LabelRed对象", description="")
public class LabelRedQuery extends Query {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "标签名称")
    private String labelName;

    @ApiModelProperty(value = "次数")
    private Long count;

    @ApiModelProperty(value = "0 人 1 设备 2 应用")
    private String objType;

    @ApiModelProperty(value = "业务场景类别 0 基于模型 1 基于规则 2 基于统计分析 3 自定义")
    private String type;

    @ApiModelProperty(value = "标签表配置id")
    private String labelConfId;

    @ApiModelProperty(value = "昨日排名")
    private Integer yesterdayRank;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getLabelConfId() {
        return labelConfId;
    }

    public void setLabelConfId(String labelConfId) {
        this.labelConfId = labelConfId;
    }
    public Integer getYesterdayRank() {
        return yesterdayRank;
    }

    public void setYesterdayRank(Integer yesterdayRank) {
        this.yesterdayRank = yesterdayRank;
    }

    @Override
    public String toString() {
        return "LabelRed{" +
            "id=" + id +
            ", labelName=" + labelName +
            ", count=" + count +
            ", objType=" + objType +
            ", type=" + type +
            ", labelConfId=" + labelConfId +
            ", yesterdayRank=" + yesterdayRank +
        "}";
    }
}
