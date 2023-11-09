package com.vrv.vap.monitor.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class AssetType {

    private String guid;
    private String iconCls;
    //实际展示 treeCode
    private String key;

    //实际展示 parent treeCode
    private String parentId;

    private String title;
    private String treeCode;
    private String type;
    private String uniqueCode;

    private List<AssetType> children;
}
