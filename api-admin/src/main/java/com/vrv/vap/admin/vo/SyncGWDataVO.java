package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2020/5/19
 * @description
 */
public class SyncGWDataVO {

    /**
     *  以下为增量
     *  org                       组织机构信息变更
     *  manager                   管理员信息变更
     *  role_privilege            角色和资源权限信息
     * manager_organizatio         人员安全域信息变更
     * ipRange                    机构ip范围变更推送接口
     *
     *
     * 以下为导入通知接口,作为全量导入通知开关
     *  auth                    需要调用全量拉取资源和权限数据接口
     *  org                     需要调用全量拉取机构数据接口
     */
    @ApiModelProperty("数据类型")
    private String dataType;

    /**
     *  add       新增
     *  edit     修改
     *  delete    删除
     *  import    导入，进行全量的操作
     */
    @ApiModelProperty("操作类型")
    private String operateType;

    /**
     * 组织机构id
     */
    private String orgId;

    /**
     * 操作数据
     */
    @ApiModelProperty("操作数据")
    private String data;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
