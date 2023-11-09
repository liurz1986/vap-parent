package com.vrv.vap.alarmdeal.business.appsys.vo;

import lombok.Data;

@Data
public class AssetTypeTemplateVO {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String guid;

    private String formdata;

    private String keyData;//  title  code

    private String name;

    private boolean deleteFlag;
}
