package com.vrv.vap.alarmdeal.business.asset.contract;

public class BaseArea {
 
    private Long id;

    /**
     * 区域编码
     */
 
    private String areaCode;

    /**
     * 区域名称
     */
 
    private String areaName;

    /**
     * ip范围
     */
 
    private String ipRange;

    /**
     * 上级编号
     */
 
    private String parentCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 截取编码（确认地区）
     */
   
    private String areaCodeSub;

    private Integer sort;

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
     * 获取ip范围
     *
     * @return ip_range - ip范围
     */
    public String getIpRange() {
        return ipRange;
    }

    /**
     * 设置ip范围
     *
     * @param ipRange ip范围
     */
    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }

    /**
     * 获取上级编号
     *
     * @return parent_code - 上级编号
     */
    public String getParentCode() {
        return parentCode;
    }

    /**
     * 设置上级编号
     *
     * @param parentCode 上级编号
     */
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
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
     * 获取截取编码（确认地区）
     *
     * @return area_code_sub - 截取编码（确认地区）
     */
    public String getAreaCodeSub() {
        return areaCodeSub;
    }

    /**
     * 设置截取编码（确认地区）
     *
     * @param areaCodeSub 截取编码（确认地区）
     */
    public void setAreaCodeSub(String areaCodeSub) {
        this.areaCodeSub = areaCodeSub;
    }

    /**
     * @return sort
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * @param sort
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}