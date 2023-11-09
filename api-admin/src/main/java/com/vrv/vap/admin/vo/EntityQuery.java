package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class EntityQuery extends Query {

    /**
     * 实体名称
     */
    @QueryLike
    @ApiModelProperty("实体名称")
    private String name;

    /**
     * 搜索提示
     */
    @QueryLike
    @ApiModelProperty("搜索提示")
    private String tip;

    /**
     * 图标路径
     */
    @ApiModelProperty("图标路径")
    private String icoid;

    /**
     * 备注
     */
    @QueryLike
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 最后修改时间
     */
    @ApiModelProperty("最后修改时间")
    private Date lastUpdateTime;

    /**
     * 实体类型
     */
    @ApiModelProperty("实体类型")
    private Integer type;


    /**
     * 获取实体名称
     *
     * @return name - 实体名称
     */

    public String getName() {
        return name;
    }

    /**
     * 设置实体名称
     *
     * @param name 实体名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取搜索提示
     *
     * @return tip - 搜索提示
     */
    public String getTip() {
        return tip;
    }

    /**
     * 设置搜索提示
     *
     * @param tip 搜索提示
     */
    public void setTip(String tip) {
        this.tip = tip;
    }

    /**
     * 获取图标路径
     *
     * @return icoId - 图标路径
     */
    public String getIcoid() {
        return icoid;
    }

    /**
     * 设置图标路径
     *
     * @param icoid 图标路径
     */
    public void setIcoid(String icoid) {
        this.icoid = icoid;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取最后修改时间
     *
     * @return last_update_time - 最后修改时间
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param lastUpdateTime 最后修改时间
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}