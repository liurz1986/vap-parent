package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("数据字典查询对象")
public class DictionaryQuery extends Query {

    @ApiModelProperty("主键")
    private String id;

    /**
     * 字典名称
     */
    @ApiModelProperty("字典名称")
    private String indexname;

    /**
     * 字典类型
     */
    @ApiModelProperty("字典类型")
    private String type;

    /**
     * 字典id
     */
    @QueryLike
    @ApiModelProperty("字典id")
    private String indexid;

    /**
     * 字典描述
     */
    @QueryLike
    @ApiModelProperty("字典描述")
    private String description;

    /**
     * 详情字段uuid
     */
    private String uuid;

    /**
     * 详情
     */
    @ApiModelProperty("字典详情")
    private String details;
    @ApiModelProperty("字典详情父id")
    private Integer pid;
    @ApiModelProperty("状态")
    private Integer state;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取字典名称
     *
     * @return indexName - 字典名称
     */
    public String getIndexname() {
        return indexname;
    }

    /**
     * 设置字典名称
     *
     * @param indexname 字典名称
     */
    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    /**
     * 获取字典类型
     *
     * @return type - 字典类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置字典类型
     *
     * @param type 字典类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取字典id
     *
     * @return indexId - 字典id
     */
    public String getIndexid() {
        return indexid;
    }

    /**
     * 设置字典id
     *
     * @param indexid 字典id
     */
    public void setIndexid(String indexid) {
        this.indexid = indexid;
    }

    /**
     * 获取字典描述
     *
     * @return description - 字典描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置字典描述
     *
     * @param description 字典描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取详情
     *
     * @return details - 详情
     */
    public String getDetails() {
        return details;
    }

    /**
     * 设置详情
     *
     * @param details 详情
     */
    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getPid() { return pid; }

    public void setPid(Integer pid) { this.pid = pid; }

    public Integer getState() { return state; }

    public void setState(Integer state) { this.state = state; }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
