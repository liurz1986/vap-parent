package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="ConfBaseWebsite对象", description="")
public class ConfBaseWebsiteQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "IP白名单 DOMAIN过滤域名后缀 TIER5统计5级url IP_BLACK黑名单")
    private String code;

    @ApiModelProperty(value = "配置内容")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String content;

    @ApiModelProperty(value = "0-关闭 1-开启")
    private Integer state;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_Time")
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "ConfBaseWebsite{" +
            "id=" + id +
            ", code=" + code +
            ", content=" + content +
            ", state=" + state +
            ", updateTime=" + updateTime +
        "}";
    }
}
