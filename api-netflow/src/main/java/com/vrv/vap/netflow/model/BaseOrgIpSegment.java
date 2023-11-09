package com.vrv.vap.netflow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@ApiModel("组织机构IP段信息")
@Table(name = "base_org_ip_segment")
public class BaseOrgIpSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("组织机构IP段id，主键")
    private Long id;

    /**
     * 区域编码
     */
    @ApiModelProperty("区域编码")
    @Column(name = "area_code")
    private String areaCode;

    /**
     * 区域名称
     */
    @ApiModelProperty("区域名称")
    @Column(name = "area_name")
    private String areaName;

    /**
     * 部门编号
     */
    @Column(name = "department_code")
    @ApiModelProperty("机构编码")
    private String departmentCode;

    /**
     * 部门名称
     */
    @Column(name = "department_name")
    @ApiModelProperty("部门名称")
    private String departmentName;

    /**
     * 父节点编码
     */
    @Column(name = "parent_code")
    @ApiModelProperty("父节点编码")
    private String parentCode;

    /**
     * 地域识别码P取值
     */
    @Column(name = "area_identi_code")
    @ApiModelProperty("地域识别码P取值")
    private Integer areaIdentiCode;

    /**
     * 网络分区段
     */
    @Column(name = "net_partition_code")
    @ApiModelProperty("网络分区段")
    private Integer netPartitionCode;

    /**
     * 开始IP地址段
     */
    @Column(name = "start_ip_segment")
    @ApiModelProperty("开始IP地址段")
    private String startIpSegment;

    /**
     * 结束IP地址段
     */
    @Column(name = "end_ip_segment")
    @ApiModelProperty("结束IP地址段")
    private String endIpSegment;

    /**
     * 开始IP地址段转换成整型
     */
    @Column(name = "start_ip_num")
    @ApiModelProperty("开始IP地址段转换成整型")
    private Long startIpNum;

    /**
     * 结束IP地址段转换成整型
     */
    @Column(name = "end_ip_num")
    @ApiModelProperty("结束IP地址段转换成整型")
    private Long endIpNum;

    /**
     * 子网掩码
     */
    @Column(name = "subnet_mask")
    @ApiModelProperty("子网掩码")
    private Byte subnetMask;

    /**
     * ip分布
     */
    @Column(name = "ip_type")
    @ApiModelProperty("ip分布")
    private Byte ipType;

    /**
     * 启用和预留的地域识别码
     */
    @Column(name = "ip_use")
    @ApiModelProperty("启用和预留的地域识别码")
    private Byte ipUse;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 是否删除
     */
    @Column(name = "is_delete")
    @ApiModelProperty("是否删除")
    private Byte isDelete;

    /**
     * 修改人id
     */
    @Column(name = "modifier_id")
    @ApiModelProperty("修改人id")
    private String modifierId;

    /**
     * 修改人name
     */
    @Column(name = "modifier_name")
    @ApiModelProperty("修改人name")
    private String modifierName;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取区域编码
     *
     * @return area_code - 区域编码
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * 设置区域编码
     *
     * @param areaCode 区域编码
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * 获取区域名称
     *
     * @return area_name - 区域名称
     */
    public String getAreaName() {
        return areaName;
    }

    /**
     * 设置区域名称
     *
     * @param areaName 区域名称
     */
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    /**
     * 获取部门编号
     *
     * @return department_code - 部门编号
     */
    public String getDepartmentCode() {
        return departmentCode;
    }

    /**
     * 设置部门编号
     *
     * @param departmentCode 部门编号
     */
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    /**
     * 获取部门名称
     *
     * @return department_name - 部门名称
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * 设置部门名称
     *
     * @param departmentName 部门名称
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * 获取父节点编码
     *
     * @return parent_code - 父节点编码
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * 设置父节点编码
     *
     * @param parentCode 父节点编码
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    /**
     * 获取地域识别码P取值
     *
     * @return area_identi_code - 地域识别码P取值
     */
    public Integer getAreaIdentiCode() {
        return areaIdentiCode;
    }

    /**
     * 设置地域识别码P取值
     *
     * @param areaIdentiCode 地域识别码P取值
     */
    public void setAreaIdentiCode(Integer areaIdentiCode) {
        this.areaIdentiCode = areaIdentiCode;
    }

    /**
     * 获取网络分区段
     *
     * @return net_partition_code - 网络分区段
     */
    public Integer getNetPartitionCode() {
        return netPartitionCode;
    }

    /**
     * 设置网络分区段
     *
     * @param netPartitionCode 网络分区段
     */
    public void setNetPartitionCode(Integer netPartitionCode) {
        this.netPartitionCode = netPartitionCode;
    }

    /**
     * 获取开始IP地址段
     *
     * @return start_ip_segment - 开始IP地址段
     */
    public String getStartIpSegment() {
        return startIpSegment;
    }

    /**
     * 设置开始IP地址段
     *
     * @param startIpSegment 开始IP地址段
     */
    public void setStartIpSegment(String startIpSegment) {
        this.startIpSegment = startIpSegment;
    }

    /**
     * 获取结束IP地址段
     *
     * @return end_ip_segment - 结束IP地址段
     */
    public String getEndIpSegment() {
        return endIpSegment;
    }

    /**
     * 设置结束IP地址段
     *
     * @param endIpSegment 结束IP地址段
     */
    public void setEndIpSegment(String endIpSegment) {
        this.endIpSegment = endIpSegment;
    }

    /**
     * 获取开始IP地址段转换成整型
     *
     * @return start_ip_num - 开始IP地址段转换成整型
     */
    public Long getStartIpNum() {
        return startIpNum;
    }

    /**
     * 设置开始IP地址段转换成整型
     *
     * @param startIpNum 开始IP地址段转换成整型
     */
    public void setStartIpNum(Long startIpNum) {
        this.startIpNum = startIpNum;
    }

    /**
     * 获取结束IP地址段转换成整型
     *
     * @return end_ip_num - 结束IP地址段转换成整型
     */
    public Long getEndIpNum() {
        return endIpNum;
    }

    /**
     * 设置结束IP地址段转换成整型
     *
     * @param endIpNum 结束IP地址段转换成整型
     */
    public void setEndIpNum(Long endIpNum) {
        this.endIpNum = endIpNum;
    }

    /**
     * 获取子网掩码
     *
     * @return subnet_mask - 子网掩码
     */
    public Byte getSubnetMask() {
        return subnetMask;
    }

    /**
     * 设置子网掩码
     *
     * @param subnetMask 子网掩码
     */
    public void setSubnetMask(Byte subnetMask) {
        this.subnetMask = subnetMask;
    }

    /**
     * 获取ip分布
     *
     * @return ip_type - ip分布
     */
    public Byte getIpType() {
        return ipType;
    }

    /**
     * 设置ip分布
     *
     * @param ipType ip分布
     */
    public void setIpType(Byte ipType) {
        this.ipType = ipType;
    }

    /**
     * 获取启用和预留的地域识别码
     *
     * @return ip_use - 启用和预留的地域识别码
     */
    public Byte getIpUse() {
        return ipUse;
    }

    /**
     * 设置启用和预留的地域识别码
     *
     * @param ipUse 启用和预留的地域识别码
     */
    public void setIpUse(Byte ipUse) {
        this.ipUse = ipUse;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取是否删除
     *
     * @return is_delete - 是否删除
     */
    public Byte getIsDelete() {
        return isDelete;
    }

    /**
     * 设置是否删除
     *
     * @param isDelete 是否删除
     */
    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * 获取修改人id
     *
     * @return modifier_id - 修改人id
     */
    public String getModifierId() {
        return modifierId;
    }

    /**
     * 设置修改人id
     *
     * @param modifierId 修改人id
     */
    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    /**
     * 获取修改人name
     *
     * @return modifier_name - 修改人name
     */
    public String getModifierName() {
        return modifierName;
    }

    /**
     * 设置修改人name
     *
     * @param modifierName 修改人name
     */
    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }
}