package com.vrv.vap.alarmdeal.business.asset.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AssetOrgTreeVO {

    private String name;

    private String code;

    private String guid;

    private List<AssetOrgTreeVO> children=new ArrayList<>();

}
