package com.vrv.vap.xc.model;

import lombok.Data;

@Data
public class BuildQuery {
    /**
     * 索引名称
     */
    private String index;

    private String[] indexes;
    /**
     * 是否排序
     */
    private boolean sort;
    /**
     * 是否关系图
     */
    private boolean diagram;

    /**
     * 是否需要按日期查询
     */
    private boolean date;
    /**
     * 是否多索引
     */
    private boolean multipleIndex;

    private String aggField;

    private int size;
    private String keyField;
    private String interval;
}
