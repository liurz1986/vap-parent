package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="ConfLookup对象", description="")
public class ConfLookupQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "字段对应类型		system:系统	website:站点	device:设备	prealarm:预警	alarm:报警")
    private String type;

    @ApiModelProperty(value = "字段对应编码")
    private String code;

    private String value;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String description;

    @ApiModelProperty(value = "状态	1:启用(默认)	0:禁用")
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ConfLookup{" +
            "id=" + id +
            ", type=" + type +
            ", code=" + code +
            ", value=" + value +
            ", description=" + description +
            ", status=" + status +
        "}";
    }
}
