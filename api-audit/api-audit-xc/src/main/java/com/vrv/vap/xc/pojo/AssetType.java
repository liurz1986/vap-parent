package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 资产类型
 */
@Data
@TableName("asset_type")
public class AssetType implements Serializable, Comparable<AssetType> {
    private static final long serialVersionUID = -3562609335013647488L;
    @TableField("Guid")
    private String guid;
    @TableField("TreeCode")
    private String treeCode;
    @TableField("uniqueCode")
    private String uniqueCode;
    @TableField("Name")
    private String name;
    @TableField("Name_en")
    private String nameEn;
    @TableField("Icon")
    private String icon;
    @TableField("monitorProtocols")
    private String monitorProtocols;
    private Integer status;
    @TableField("orderNum")
    private Integer orderNum;
    //是否是内置数据  默认值 false
    private Boolean predefine;

    @Override
    public int compareTo(AssetType o) {
        return this.getName().compareTo(o.getName());
    }

}
